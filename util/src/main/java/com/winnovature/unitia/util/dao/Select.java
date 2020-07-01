package com.winnovature.unitia.util.dao;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.winnovature.unitia.util.db.Close;
import com.winnovature.unitia.util.db.QueueDBConnection;
import com.winnovature.unitia.util.misc.MapKeys;

public class Select {

	public List<Map<String, String>> getData(String tablename) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultset = null;
		try {

			connection = QueueDBConnection.getInstance().getConnection();
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(" select * from " + tablename + "_lock for UPDATE");
			resultset = statement.executeQuery();
			if (resultset.next()) {

				return getRecords(tablename);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}

		return null;
	}

	private List<Map<String, String>> getRecords(String tablename) {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultset = null;
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		try {

			connection = QueueDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(
					" select data from " + tablename + " where scheduletime < ? and pstatus=0 limit 500");
			statement.setLong(1, System.currentTimeMillis());
			resultset = statement.executeQuery();

			if (resultset.next()) {

				byte[] Bytes = resultset.getBytes("data");

				ByteArrayInputStream bis = new ByteArrayInputStream(Bytes);

				ObjectInputStream ois = new ObjectInputStream(bis);

				result.add((Map<String, String>) ois.readObject());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			Close.close(resultset);
			Close.close(statement);
			Close.close(connection);
		}

		if (result.size() > 0) {

			update(tablename, result);
		}
		return result;
	}

	private void update(String tablename, List<Map<String, String>> result) {

		Connection connection = null;
		PreparedStatement statement = null;
		try {

			connection = QueueDBConnection.getInstance().getConnection();
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(" update " + tablename + " set pstatus=1 where msgid = ?");

			for (int i = 0; i < result.size(); i++) {

				Map<String, String> data = result.get(i);
				statement.setString(1, data.get(MapKeys.MSGID));
				statement.addBatch();
			}

			statement.executeBatch();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			Close.close(statement);
			Close.close(connection);
		}
	}

	public void delete(String tablename, String msgid) {

		Connection connection = null;
		PreparedStatement statement = null;
		try {

			connection = QueueDBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(" delete from " + tablename + "  where msgid = ?");
			statement.setString(1, msgid);
			statement.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			Close.close(statement);
			Close.close(connection);
		}

	}
}
