package oop.grp1.Model;

public class ChatResponse {
    private long searchTime;
    private long responseTime;
    private String responseContent;
    private String session;
    private String userQuery;

    public ChatResponse(long searchTime, long responseTime, String responseContent, String session, String userQuery) {
        this.searchTime = searchTime;
        this.responseTime = responseTime;
        this.responseContent = responseContent;
        this.session = session;
        this.userQuery = userQuery;
    }

    public long getSearchTime() {
        return searchTime;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public String getSession() {
        return session;
    }
    
    public String getUserQuery() {
        return userQuery;
    }

    public void setSearchTime(long searchTime) {
        this.searchTime = searchTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public void setSession(String session) {
        this.session = session;
    }
    
}
