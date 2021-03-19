package unitiahttpd;

import java.util.List;

public class SMS {

	private String scheduletime;
	private String from;
	private String entityid;
	private String templateid;
	private String content;
	private String param1;
	private String param2;
	private String param3;
	private String param4;
	private List<String> tolist;
	public String getScheduletime() {
		return scheduletime;
	}
	public void setScheduletime(String scheduletime) {
		this.scheduletime = scheduletime;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getEntityid() {
		return entityid;
	}
	public void setEntityid(String entityid) {
		this.entityid = entityid;
	}
	public String getTemplateid() {
		return templateid;
	}
	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getParam1() {
		return param1;
	}
	public void setParam1(String param1) {
		this.param1 = param1;
	}
	public String getParam2() {
		return param2;
	}
	public void setParam2(String param2) {
		this.param2 = param2;
	}
	public String getParam3() {
		return param3;
	}
	public void setParam3(String param3) {
		this.param3 = param3;
	}
	public String getParam4() {
		return param4;
	}
	public void setParam4(String param4) {
		this.param4 = param4;
	}
	public List<String> getTolist() {
		return tolist;
	}
	public void setTolist(List<String> tolist) {
		this.tolist = tolist;
	}
	
		
}
