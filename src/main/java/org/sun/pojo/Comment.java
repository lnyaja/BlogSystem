package org.sun.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.util.Date;

@Entity
@Table ( name ="tb_comment" )
public class Comment {

  	@Id
	private String id;
  	@Column(name = "parent_content" )
	private String parentContent;
  	@Column(name = "article_id" )
	private String articleId;
  	@Column(name = "content" )
	private String content;
  	@Column(name = "user_id" )
	private String userId;
  	@Column(name = "user_avatar" )
	private String userAvatar;
  	@Column(name = "user_name" )
	private String userName;
  	@Column(name = "state" )
	private String state = "1";
  	@Column(name = "create_time" )
	private Date createTime;
  	@Column(name = "update_time" )
	private Date updateTime;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getParentContent() {
		return parentContent;
	}

	public void setParentContent(String parent_content) {
		this.parentContent = parent_content;
	}


	public String getArticleId() {
		return articleId;
	}

	public void setArticleId(String article_id) {
		this.articleId = article_id;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String user_id) {
		this.userId = user_id;
	}


	public String getUserAvatar() {
		return userAvatar;
	}

	public void setUserAvatar(String user_avatar) {
		this.userAvatar = user_avatar;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String user_name) {
		this.userName = user_name;
	}


	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}


	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.sql.Timestamp create_time) {
		this.createTime = create_time;
	}


	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(java.sql.Timestamp update_time) {
		this.updateTime = update_time;
	}

}
