package com.winnovature.unitia.util.http;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;
import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.MapKeys;

public class HTTPDeliveryTimeCheck {



	/*
	 * possible return values are INVALID,CURRENT,SCHEDULE
	 */
	public String isValidScheduleTime(Map<String,Object> msgmap) {
		String format = "yyyy/MM/dd/HH/mm";
		String status = null;
		Date scheduleDate = null;
		String scheduleTime =(String) msgmap.get(MapKeys.SCHEDULE_TIME_STRING);
		String username = (String) msgmap.get(MapKeys.USERNAME);
		String mobile= (String) msgmap.get(MapKeys.MOBILE);
		try {
			SimpleDateFormat formater = new SimpleDateFormat(format);
			formater.setLenient(false);
			try {
				scheduleDate = formater.parse(scheduleTime);
				msgmap.put(MapKeys.SCHEDULE_TIME, ""+scheduleDate.getTime());
				
				
			} catch (ParseException pe) {
				status = "INVALID";
				return status;
			}

			Map<String,String> partnerDetails = PushAccount.instance().getPushAccount(username);
			
			String msgclass=partnerDetails.get(MapKeys.MSGCLASS);

			if ("1".equals(msgclass)||"5".equals(msgclass)||"3".equals(msgclass)) {
			
				status = isValidScheduleTime(msgmap, scheduleDate);

			} else if ("2".equals(msgclass)) {
			
				if (mobile.length() == 12
						&&mobile.startsWith("91")) {
					/*
					 * Mobile Number Considered as Indian Number we need to do
					 * the TRAI Blockout also.
					 */

					status = isScheduleTRAIBlockoutTime(msgmap, scheduleDate);

					if (status.equals("BLOCKOUT")) {
						status = isValidScheduleTime(msgmap);
					} else {
						status = isValidScheduleTime(msgmap, scheduleDate);
					}
				} else {
					/*
					 * Mobile Number Considered as Foriegn Number no need to do
					 * the TRAI Blockout also.
					 */
					status = isValidScheduleTime(msgmap, scheduleDate);
				}

			} else {
				/*
				 * There is no more validation are not taken for further msg
				 * class Message Considered as Current Message
				 */
				status = "CURRENT";
			}

		} catch (Exception e) {
			msgmap.put("schedule exception", ErrorMessage.getMessage(e));
			status = "EXCEPTION";
		}
	

		return status;
	}

	private String isValidScheduleTime(Map<String,Object> msgmap, Date scheduleDate) {

			String status = null;
		Date current_date = new Date();

		long scheMins = (scheduleDate.getTime() / 60000)
				- (current_date.getTime() / 60000);

		
		if (scheMins < Integer.parseInt(ConfigParams.getInstance().getProperty(ConfigKey.MAX_SCHEDULE_TIME_ALLOWED_MINS))) {
			status = "SCHEDULE";
		

		}else {
			status = "INVALID";
		}

		
		return status;

	}

	private String isScheduleTRAIBlockoutTime(Map<String,Object> msgmap,
			Date scheduleDate) throws Exception {

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


			msgmap.put(MapKeys.SCHEDULE_TIME, ""+block_end_time.getTime());

			status = "BLOCKOUT";

		}

		return status;
	}

	private String isTRAIBlockoutTime(Map<String,String> msgmap,
			Date scheduleDate) throws Exception {

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

			msgmap.put(MapKeys.SCHEDULE_TIME, ""+block_end_time.getTime());

			status = "BLOCKOUT";

		}

		return status;
	}

	/*
	 * possible return values are CURRENT,INVALID,BLOCKOUT
	 */
	public String isValidBlockOut(Map<String,String> msgmap) {

		String status = "CURRENT";

		try {

			String mobile=msgmap.get(MapKeys.MOBILE);
			String username=msgmap.get(MapKeys.USERNAME);

			if (mobile.length() == 12
					&& mobile.startsWith("91")) {
				/*
				 * Mobile Number Considered as Indian Number
				 */

				Map<String,String> partnerDetails = PushAccount.instance().getPushAccount(username);

				String msgclass=partnerDetails.get(MapKeys.MSGCLASS);
				String blockout_yn=partnerDetails.get(MapKeys.BLOCKOUT_YN);

				
				if ("1".equals(msgclass)) {
					/*
					 * Message considered as Transactional Message
					 */

					if ("0".equals(blockout_yn)) {
						/*
						 * Blockout is disabled so message considered as Current
						 * Message
						 */
						return "CURRENT";
					} else {
						/*
						 * Blockout is enabled ,do the Blocklout validation
						 */
						String startTime = partnerDetails.get(MapKeys.BLOCKOUT_START_TIME);
						String endTime = partnerDetails.get(MapKeys.BLOCKOUT_END_TIME);
						return isBlockOut(msgmap, startTime, endTime);
					}

				} else if ("2"
						.equals(msgclass)) {
					/*
					 * Message considered as Promotional Message
					 */

					String startTime = ConfigParams.getInstance().getProperty(ConfigKey.TRAI_BLOCKLOUT_START);
					String endTime = ConfigParams.getInstance().getProperty(ConfigKey.TRAI_BLOCKLOUT_END);
					status = isBlockOut(msgmap, startTime, endTime);

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
							return isBlockOut(msgmap, startTime, endTime);
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
			status = "INVALID";
		}

		return status;
	}

	private String isBlockOut(Map<String,String> msgmap,
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


			msgmap.put(MapKeys.SCHEDULE_TIME, ""+block_end_time.getTime());
		
			
			status = "BLOCKOUT";

		} else if (Calendar.getInstance().before(block_start_time)
				|| Calendar.getInstance().after(block_end_time)) {
			status = "CURRENT";
			msgmap.put(MapKeys.SCHEDULE_TIME, "");
		} else {
			status = "CURRENT";
			msgmap.put(MapKeys.SCHEDULE_TIME, "");
			
		}
		return status;
	}
}
