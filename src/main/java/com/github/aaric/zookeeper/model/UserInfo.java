package com.github.aaric.zookeeper.model;

/**
 * 用户信息
 *
 * @author Aaric, created on 2018-03-19T17:14.
 * @since 0.0.1-SNAPSHOT
 */
public class UserInfo {

    private String account;
    private String password;

    public UserInfo() {
    }

    public UserInfo(String account, String password) {
        this.account = account;
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
