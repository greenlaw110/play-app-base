package org.osgl.play;

import org.osgl.util.S;
import play.Play;
import play.libs.Crypto;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 2/10/12
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class Auth {

    public static class Token implements Serializable {
        public String oid;
        @Deprecated
        public boolean outdated;
        public boolean expired;
        void setExpired() {
            outdated = true;
            expired = true;
        }
        public List<String> payload = new ArrayList<String>();

        public boolean isEmpty() {
            return S.isEmpty(oid);
        }
    }

    public static enum TokenLife {
        /**
         * short life token live for 1 hour
         */
        SHORT(60 * 60),
        /**
         * Normal life token live for 1 day
         */
        NORMAL(60 * 60 * 24),
        /**
         * Long life token live for 90 days
         */
        LONG(60 * 60 * 24 * 90);
        private long seconds;
        private TokenLife(long seconds) {
            this.seconds = seconds;
        }

        /**
         * Return the due time in time millis
         * @return
         */
        public long due() {
            long now = System.currentTimeMillis();
            long period = seconds * 1000;
            return now + (period - now % period);
        }
    }

    public static String generateSecret() {
        String s = UUID.randomUUID().toString();
        return Crypto.sign(s);
    }

    public static String generateToken(String oid, String... payload) {
        return generateToken(TokenLife.SHORT, oid, payload);
    }

    public static String generateToken(TokenLife tl, String oid, String... payload) {
        long due = tl.due();
        List<String> l = new ArrayList<String>(2 + payload.length);
        l.add(oid);
        l.add(String.valueOf(due));
        l.addAll(Arrays.asList(payload));
        String s = S.join("|", l);
        return Crypto.encryptAES(s);
    }

    /**
     * Return a list of string. The first item is the oid, rest are the payload
     *
     * return empty list if error encountered
     * @param token
     * @return
     */
    public static Token parseToken(String token) {
        Token tk = new Token();
        if (S.isEmpty(token)) return tk;
        String s = "";
        try {
            s = Crypto.decryptAES(token);
        } catch (Exception e) {
            return tk;
        }
        String[] sa = s.split("\\|");
        if (sa.length < 2) return tk;
        tk.oid = sa[0];
        try {
            long due = Long.parseLong(sa[1]);
            if (due <= System.currentTimeMillis()) {
                tk.setExpired();
                return tk;
            }
        } catch (Exception e) {
            tk.setExpired();
            return tk;
        }
        if (sa.length > 2) {
            sa = Arrays.copyOfRange(sa, 2, sa.length);
            tk.payload.addAll(Arrays.asList(sa));
        }
        return tk;
    }

    public static boolean isTokenValid(String oid, String token) {
        if (S.isEmpty(oid)) return false;
        if (S.isEmpty(token)) return false;
        String s = Crypto.decryptAES(token);
        String[] sa = s.split("\\|");
        if (sa.length < 2) return false;
        if (!S.isEqual(oid, sa[0])) return false;
        try {
            long due = Long.parseLong(sa[1]);
            return (due > System.currentTimeMillis());
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        Play.configuration = new Properties();
        Play.configuration.put("application.secret", UUID.randomUUID().toString());
        String oid = UUID.randomUUID().toString();
        String token = generateToken(oid);
        System.out.println(token);
        System.out.println(isTokenValid(oid, token));
        Object lock = new Object();
        synchronized (lock) {
            try {
                lock.wait(3000);
            } catch (Exception e) {
                // ignore
            }
        }
        String token2 = generateToken(oid);
        System.out.println(S.isEqual(token, token2));
        token = generateToken(oid, "xyz", "aaa");
    }
}
