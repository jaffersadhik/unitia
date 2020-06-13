package com.winnovature.unitia.util.deliverytime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.winnovature.unitia.util.constants.DeferType;
import com.winnovature.unitia.util.datacache.account.PushAccount;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;

public class MapDeliveryTimeCheck {

	private static Log logger = LogFactory.getLog(MapDeliveryTimeCheck.class);

	private final String className = "[com.a2wi.ng.util.deliverytime.MapDeliveryTimeCheck] ";



	private String isTRAIBlockoutTime(Date scheduleDate) throws Exception {

		String startTime = ConfigParams.getInstance().getProperty(ConfigKey.TRAI_BLOCKLOUT_START);
		String endTime = ConfigParams.getInstance().getProperty(ConfigKey.TRAI_BLOCKLOUT_END);

		String status = "CURRENT";
		Calendar block_start_time = Calendar.getInstance();
		block_start_time.setTime(scheduleDate);
		Calendar block_end_time = Calendar.getInstance();
		block_end_time.setTime(scheduleDate);
		// st - start time
		StringTokenizer st = new StringTokenizer(startTime, ":");
		StringTokenizer et = new StringTokenizer(endTime, ":");

		// st_hr - starting hour
		// st_min - starting mins
		int st_hr = Integer.parseInt(st.nextToken());
		int st_min = Integer.parseInt(st.nextToken());
		int et_hr = Integer.parseInt(et.nextToken());
		int et_min = Integer.parseInt(et.nextToken());

		Calendar date = Calendar.getInstance();
		date.setTime(scheduleDate);
		int ct_hr = date.get(Calendar.HOUR_OF_DAY);

		if (st_hr > et_hr) {
			if (st_hr > ct_hr) {

				block_start_time.set(Calendar.DAY_OF_MONTH, (date
						.get(Calendar.DAY_OF_MONTH) - 1));
				block_end_time.set(Calendar.DAY_OF_MONTH, date
						.get(Calendar.DAY_OF_MONTH));

			} else {
				block_start_time.set(Calendar.DAY_OF_MONTH, date
						.get(Calendar.DAY_OF_MONTH));
				block_end_time.set(Calendar.DAY_OF_MONTH, (date
						.get(Calendar.DAY_OF_MONTH) + 1));
			}
		} else {
			block_start_time.set(Calendar.DAY_OF_MONTH, date
					.get(Calendar.DAY_OF_MONTH));
			block_end_time.set(Calendar.DAY_OF_MONTH, date
					.get(Calendar.DAY_OF_MONTH));
		}

		block_start_time.set(Calendar.HOUR_OF_DAY, st_hr);
		block_start_time.set(Calendar.MINUTE, st_min);
		block_end_time.set(Calendar.HOUR_OF_DAY, et_hr);
		block_end_time.set(Calendar.MINUTE, et_min);

		Calendar currentTime = Calendar.getInstance();
		currentTime.setTime(scheduleDate);
		if (currentTime.after(block_start_time)
				&& currentTime.before(block_end_time)) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
			Calendar cal_date = Calendar.getInstance();

			cal_date.set(Calendar.HOUR_OF_DAY, et_hr);
			cal_date.set(Calendar.MINUTE, et_min);

			String str = df.format(block_end_time.getTime());

			status = "BLOCKOUT";

		} else if (currentTime.before(block_start_time)
				|| currentTime.after(block_end_time)) {
			status = "CURRENT";
		} else {
			status = "CURRENT";
		}
		return status;
	}

	/*
	 * possible return values are CURRENT,INVALID,BLOCKOUT
	 */
	public String isValidBlockOut(Map requestObject) {

		String status = "CURRENT";

		try {

			/*
			 * before coming to this method 91 should be appended for all Indian
			 * Number's here the mobile number should be the Internalnumber
			 * format
			 */
			if (((String) requestObject.get("mobile")).length() == 12
					&& ((String) requestObject.get("mobile")).startsWith("91")) {
				/*
				 * Mobile Number Considered as Indian Number
				 */

				Map partnerDetails = PushAccount.instance().getPushAccount(requestObject.get("userid").toString());

				if ("1".equals(partnerDetails.get("MSGCLASS").toString())) {
					/*
					 * Message considered as Transactional Message
					 */

					if ("0"
							.equals(partnerDetails.get("BLOCKOUT_YN")
									.toString())) {
						/*
						 * Blockout is disabled so message considered as Current
						 * Message
						 */
						return "CURRENT";
					} else {
						/*
						 * Blockout is enabled ,do the Blocklout validation
						 */
						String startTime = partnerDetails.get("BLOCKOUT_START")
								.toString();
						String endTime = partnerDetails.get("BLOCKOUT_END")
								.toString();

						return isBlockOut("" + DeferType.TRANS_BLOCKOUT,
								requestObject, startTime, endTime);
					}

				} else if ("2"
						.equals(partnerDetails.get("msgclass").toString())) {
					/*
					 * Message considered as Promotional Message
					 */

					String startTime = ConfigParams.getInstance().getProperty(ConfigKey.TRAI_BLOCKLOUT_START);
					String endTime = ConfigParams.getInstance().getProperty(ConfigKey.TRAI_BLOCKLOUT_START);
					status = isBlockOut("" + DeferType.TRAI_BLOCKOUT,
							requestObject, startTime, endTime);

					if (status.equals("CURRENT")) {

						if ("0".equals(partnerDetails.get("blockout_yn")
								.toString())) {
							/*
							 * Blockout is disabled so message considered as
							 * Current Message
							 */
							return "CURRENT";
						} else {
							/*
							 * Blockout is enabled ,do the Blocklout validation
							 */
							startTime = partnerDetails.get("blockout_start")
									.toString();
							endTime = partnerDetails.get("blockout_end")
									.toString();
							return isBlockOut("" + DeferType.TRANS_BLOCKOUT,
									requestObject, startTime, endTime);
						}
					}

				} else {
					/*
					 * There is no more validation are not taken for further msg
					 * class Message Considered as Current Message
					 */
					return "CURRENT";
				}

			} else {
				/*
				 * Mobile Number Considered as foriegn number so Message not
				 * blocked
				 */
				status = "CURRENT";
			}
		} catch (Exception e) {
			logger.error(className + "Exception @ isValidBlockOut() ", e);
			status = "INVALID";
		}

		return status;
	}

	private String isBlockOut(String deferType, Map requestObject,
			String startTime, String endTime) throws Exception {

		String status = "CURRENT";
		Calendar block_start_time = Calendar.getInstance();
		Calendar block_end_time = Calendar.getInstance();

		// st - start time
		StringTokenizer st = new StringTokenizer(startTime, ":");
		StringTokenizer et = new StringTokenizer(endTime, ":");

		// st_hr - starting hour
		// st_min - starting mins
		int st_hr = Integer.parseInt(st.nextToken());
		int st_min = Integer.parseInt(st.nextToken());
		int et_hr = Integer.parseInt(et.nextToken());
		int et_min = Integer.parseInt(et.nextToken());

		Calendar date = Calendar.getInstance();

		int ct_hr = date.get(Calendar.HOUR_OF_DAY);

		if (st_hr > et_hr) {
			if (st_hr > ct_hr) {

				block_start_time.set(Calendar.DAY_OF_MONTH, (date
						.get(Calendar.DAY_OF_MONTH) - 1));
				block_end_time.set(Calendar.DAY_OF_MONTH, date
						.get(Calendar.DAY_OF_MONTH));

			} else {
				block_start_time.set(Calendar.DAY_OF_MONTH, date
						.get(Calendar.DAY_OF_MONTH));
				block_end_time.set(Calendar.DAY_OF_MONTH, (date
						.get(Calendar.DAY_OF_MONTH) + 1));
			}
		} else {
			block_start_time.set(Calendar.DAY_OF_MONTH, date
					.get(Calendar.DAY_OF_MONTH));
			block_end_time.set(Calendar.DAY_OF_MONTH, date
					.get(Calendar.DAY_OF_MONTH));
		}

		block_start_time.set(Calendar.HOUR_OF_DAY, st_hr);
		block_start_time.set(Calendar.MINUTE, st_min);
		block_end_time.set(Calendar.HOUR_OF_DAY, et_hr);
		block_end_time.set(Calendar.MINUTE, et_min);

		if (Calendar.getInstance().after(block_start_time)
				&& Calendar.getInstance().before(block_end_time)) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
			Calendar cal_date = Calendar.getInstance();

			cal_date.set(Calendar.HOUR_OF_DAY, et_hr);
			cal_date.set(Calendar.MINUTE, et_min);

			String str = df.format(block_end_time.getTime());

			requestObject.put("SCHETS", str);
			requestObject.put("DEFER_TYPE", deferType);
			requestObject.put("BLOCKOUT_YN", "1");

			status = "BLOCKOUT";

		} else if (Calendar.getInstance().before(block_start_time)
				|| Calendar.getInstance().after(block_end_time)) {
			status = "CURRENT";
		} else {
			status = "CURRENT";
		}
		return status;
	}
}
