package org.osgl.play;

import org.osgl.util.Crypto;
import org.osgl.util.S;
import play.Play;
import play.cache.Cache;

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
        public long due;
        void setExpired() {
            outdated = true;
            expired = true;
        }
        public List<String> payload = new ArrayList<String>();

        public boolean isEmpty() {
            return S.isEmpty(oid);
        }

        public boolean consumed() {
            return Cache.get("auth-tk-consumed-" + (oid + due)) != null;
        }

        public void consume() {
            Cache.add("auth-tk-consumed-" + (oid + due), "true", (due + 1000 - System.currentTimeMillis())/1000 + "s");
        }
    }

    public static enum TokenLife {
        /**
         * very short life token that live for only 1 min
         */
        ONE_MIN(60),
        /**
         * short life token live for 1 hour
         */
        SHORT(60 * 60),
        ONE_HOUR(60 * 60),
        /**
         * Normal life token live for 1 day
         */
        NORMAL(60 * 60 * 24),
        ONE_DAY(60 * 60 * 24),
        ONE_WEEK(60 * 60 * 24 * 7),
        THIRTY_DAYS(60 * 60 * 24 * 30),
        /**
         * Long life token live for 90 days
         */
        LONG(60 * 60 * 24 * 90),
        NINETY_DAYS(60 * 60 * 24 * 90);
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
            //return now + (period - now % period);
            return now + period;
        }
    }

    public static String generateSecret() {
        String s = UUID.randomUUID().toString();
        return Crypto.sign(s, Play.secretKey.getBytes());
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
        return Crypto.encryptAES(s, Play.secretKey);
    }

    public static String generateToken(String privateKey, TokenLife tl, String oid, String... payload) {
        long due = tl.due();
        List<String> l = new ArrayList<String>(2 + payload.length);
        l.add(oid);
        l.add(String.valueOf(due));
        l.addAll(Arrays.asList(payload));
        String s = S.join("|", l);
        if (S.empty(privateKey)) {
            privateKey = Play.secretKey;
        }
        return Crypto.encryptAES(s, privateKey);
    }

    /**
     * Return a list of string. The first item is the oid, rest are the payload
     *
     * return empty list if error encountered
     * @param token
     * @return
     */
    public static Token parseToken(String token) {
        return parseToken(null, token);
    }

    public static Token parseToken(String privateKey, String token) {
        Token tk = new Token();
        if (S.isEmpty(token)) return tk;
        String s = "";
        try {
            if (S.empty(privateKey)) {
                privateKey = Play.secretKey;
            }
            s = Crypto.decryptAES(token, privateKey);
        } catch (Exception e) {
            return tk;
        }
        String[] sa = s.split("\\|");
        if (sa.length < 2) return tk;
        tk.oid = sa[0];
        try {
            long due = Long.parseLong(sa[1]);
            tk.due = due;
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
        return isTokenValid(Play.secretKey, oid, token);
    }

    public static boolean isTokenValid(String privateKey, String oid, String token) {
        if (S.isEmpty(oid)) return false;
        if (S.isEmpty(token)) return false;
        if (S.empty(privateKey)) privateKey = Play.secretKey;
        String s = Crypto.decryptAES(token, privateKey);
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
