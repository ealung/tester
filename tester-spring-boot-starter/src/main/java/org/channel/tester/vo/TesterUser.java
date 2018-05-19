package org.channel.tester.vo;

/**
 * @author zhangchanglu<hzzhangchanglu @ corp.netease.com>
 * @since 2018/05/17 17:45.
 */
public class TesterUser {
    private String userName;
    private String userPwd;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    @Override
    public String toString() {
        return "TesterUser{" +
                "userName='" + userName + '\'' +
                ", userPwd='" + userPwd + '\'' +
                '}';
    }
}
