package com.chuckbolin.weblinkmanager;

public class WebItem {
	private int id;
	private String url;
    private String description;
 
    public WebItem(){}
    
    public WebItem(String url, String description) {
        super();
        //this.id = id;
        this.url = url;
        this.description = description;
    }

    public WebItem(int id, String url, String description) {
        super();
        this.id = id;
        this.url = url;
        this.description = description;
    }
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
