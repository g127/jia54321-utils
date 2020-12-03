package com.jia54321.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Map;

public class LdapUtil {
    static final Logger log = LoggerFactory.getLogger(LdapUtil.class);

    private final static String FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

    /**
     * 连接 ldapConnect
     * @param url 地址      例如： ldap://119.3.188.6:30389/dc=example,dc=org
     * @param principal 用户   例如： cn=admin,dc=example,dc=org
     * @param credentials  密码 123456
     * @return 结果
     */
    public static LdapContext ldapConnect(String url, String principal, String credentials) {
        return open(url, "simple", principal, credentials);
    }

    /**
     * 连接 ldapConnect
     * @param url 地址      例如： ldap://119.3.188.6:30389/dc=example,dc=org
     * @param authentication  默认应该为simple
     * @param principal 用户   例如： cn=admin,dc=example,dc=org
     * @param credentials  密码 123456
     * @return 结果
     */
    public static LdapContext open(String url, String authentication, String principal, String credentials) {
        // 获取LDAP配置信息, 执行LDAP连接
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, FACTORY);
        env.put(Context.PROVIDER_URL, url);
        env.put(Context.SECURITY_AUTHENTICATION, authentication);
        env.put(Context.SECURITY_PRINCIPAL, principal); // 账户
        env.put(Context.SECURITY_CREDENTIALS, credentials); // 密码
        LdapContext ldapContext = null;
        Control[] connCtls = null;
        try {
            ldapContext = new InitialLdapContext(env, connCtls);
            log.info("连接成功");
        } catch (javax.naming.AuthenticationException e) {
            log.error("连接失败", e);
        } catch (Exception e) {
            log.error("连接出错", e);
        }
        return ldapContext;
    }

    // ignore
    /**
     * 关闭 ldapContext. ldapContext可空，任何NamingException的将被忽略。
     *
     * @param ldapContext 要关闭的LdapContext.
     */
    public static void closeQuietly(LdapContext ldapContext) {
        if (ldapContext == null) {
            return;
        }
        try {
            ldapContext.close();
        } catch (NamingException e) {
            // ignore
        }
    }

}
