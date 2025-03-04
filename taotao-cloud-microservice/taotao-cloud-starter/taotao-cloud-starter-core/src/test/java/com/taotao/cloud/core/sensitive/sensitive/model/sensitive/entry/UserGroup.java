package com.taotao.cloud.core.sensitive.sensitive.model.sensitive.entry;


import com.taotao.cloud.core.sensitive.sensitive.annotation.Sensitive;
import com.taotao.cloud.core.sensitive.sensitive.annotation.SensitiveEntry;
import com.taotao.cloud.core.sensitive.sensitive.core.api.strategory.StrategyPassword;
import com.taotao.cloud.core.sensitive.sensitive.model.sensitive.User;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserGroup {

    /**
     * 不参与脱敏的用户
     */
    private User coolUser;

    @SensitiveEntry
    private User user;

    @SensitiveEntry
    private List<User> userList;

    @SensitiveEntry
    private Set<User> userSet;

    @SensitiveEntry
    private Collection<User> userCollection;

    /**
     * SensitiveEntry 注解不会生效
     * Sensitive 注解正常执行
     */
    @Sensitive(strategy = StrategyPassword.class)
    @SensitiveEntry
    private String password;

    /**
     * SensitiveEntry 注解不会生效
     */
    @SensitiveEntry
    private Map<String, User> userMap;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getCoolUser() {
        return coolUser;
    }

    public void setCoolUser(User coolUser) {
        this.coolUser = coolUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public Set<User> getUserSet() {
        return userSet;
    }

    public void setUserSet(Set<User> userSet) {
        this.userSet = userSet;
    }

    public Collection<User> getUserCollection() {
        return userCollection;
    }

    public void setUserCollection(Collection<User> userCollection) {
        this.userCollection = userCollection;
    }

    public Map<String, User> getUserMap() {
        return userMap;
    }

    public void setUserMap(Map<String, User> userMap) {
        this.userMap = userMap;
    }

    @Override
    public String toString() {
        return "UserGroup{" +
                "coolUser=" + coolUser +
                ", user=" + user +
                ", userList=" + userList +
                ", userSet=" + userSet +
                ", userCollection=" + userCollection +
                ", password='" + password + '\'' +
                ", userMap=" + userMap +
                '}';
    }
}
