package org.osgl.html;


import org.osgl.util.S;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColumnMeta {
	public String content;
	public String className;
	public String field;
	public boolean sortable = true;
	public int max;

	@Override
	public String toString() {
		return String.format("%1$s{class:%2$s,max:%3$s}", content, className, max);
	}

	public String getClassStr() {
		return String.format("class='%1$s'", className == null ? content.toLowerCase() : className);
	}

	public String getClassStr(String additional) {
		if (null == additional || additional.trim().equals("")) return getClassStr();
		return String.format("class='%1$s %2$s'", className == null ? content.toLowerCase() : className, additional);
	}

	public String getContent() {
		return max == 0 ? content : S.maxLength(content, max);
	}

	public String getTitleStr() {
		if (max == 0) {
			return "";
		} else {
			return String.format("title=\"%1$s\"", content);
		}
	}

	public boolean isSortable() {
		return sortable;
	}

	private static final Pattern p_ = Pattern.compile("([\\w\\s_\\-]+)");
	private static final Pattern pEx_ = Pattern.compile("\\[(.*)\\]");
	private static final Pattern pCls_ = Pattern.compile("\\bclass:\\s*([\\w\\s_\\-]+)\\b");
	private static final Pattern pMax_ = Pattern.compile("\\bmax:\\s*([\\d]+?)\\b");
	private static final Pattern pNoSort_ = Pattern.compile("\\bnosort");
	private static final Pattern pField_ = Pattern.compile("\\bfield:\\s*([\\w\\.]+)\\b");
	public static ColumnMeta valueOf(String s) {
		ColumnMeta hm = new ColumnMeta();
		Matcher m = p_.matcher(s);
		if (m.find()) {
			hm.content = m.group(1);
			hm.field = hm.content.toLowerCase();
		}
		m = pEx_.matcher(s);
		if (m.find()) {
			String ex = m.group(1);
			Matcher mCls = pCls_.matcher(ex);
			if (mCls.find()) {
				hm.className = mCls.group(1);
			}
			Matcher mMax = pMax_.matcher(ex);
			if (mMax.find()) {
				hm.max = Integer.parseInt(mMax.group(1));
			}
			Matcher mNoSort = pNoSort_.matcher(ex);
			hm.sortable = (!mNoSort.find());
			Matcher mField = pField_.matcher(ex);
			if (mField.find()) {
				hm.field = mField.group(1);
			}
		}
		return hm;
	}

	public static void main(String[] args) {
		String s = "Order Number{class: order-number ibm,max: 5}";
		System.out.println(ColumnMeta.valueOf(s).getClassStr());
	}
}
