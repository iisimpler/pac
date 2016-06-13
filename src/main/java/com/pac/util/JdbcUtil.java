package com.pac.util;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JdbcUtil {

	private static ComboPooledDataSource cpds = new ComboPooledDataSource();

	static {
		Properties props = new Properties();

		try {
			props.load(new FileInputStream("jdbc.properties"));

			cpds.setDriverClass(props.getProperty("driver"));
			cpds.setJdbcUrl(props.getProperty("url"));
			cpds.setUser(props.getProperty("user"));
			cpds.setPassword(props.getProperty("password"));
			
			cpds.setMaxPoolSize(Integer.valueOf(props.getProperty("maxPoolSize")));
			cpds.setMinPoolSize(Integer.valueOf(props.getProperty("minPoolSize")));
			cpds.setInitialPoolSize(Integer.valueOf(props.getProperty("initialPoolSize")));
			cpds.setAcquireIncrement(Integer.valueOf(props.getProperty("acquireIncrement")));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static <T> List<T> select(T t, Map<String, Object> where) {

		try {
			// 根据传入对象获取相应类
			Class<?> clazz = t.getClass();
			// 根据类获取其中的字段
			Field[] fields = clazz.getDeclaredFields();

			// 拼接查询语句
			StringBuilder sql = new StringBuilder();

			if (where != null && where.size() != 0) {
				for (String w : where.keySet()) {
					sql.append(" and " + w + " = ?");
				}
				sql = new StringBuilder(" where ").append(sql.substring(4));
			}

			sql = new StringBuilder("select * from `" + clazz.getSimpleName().toLowerCase() + "`").append(sql);

			// 获取连接
			Connection conn = cpds.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			if (where != null && where.size() != 0) {
				int temp = 0;
				for (String w : where.keySet()) {
					temp++;
					pstmt.setObject(temp, where.get(w));
				}
			}

			ResultSet rs = pstmt.executeQuery();

			List<T> list = new ArrayList<T>();
			while (rs.next()) {
				T tt = (T) clazz.newInstance();

				int temp2 = 0;
				for (Field field : fields) {
					field.setAccessible(true);
					temp2++;

					if (field.getType().toString().contains("Integer")) {
						field.set(tt, rs.getInt(temp2));
					}
					if (field.getType().toString().contains("String")) {
						field.set(tt, rs.getString(temp2));
					}
					if (field.getType().toString().contains("Date")) {
						field.set(tt, new Date(rs.getDate(temp2).getTime()));
					}
				}

				list.add(tt);
			}
			conn.close();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> int insert(T t) {

		try {
			// 根据传入对象获取相应类
			Class<?> clazz = t.getClass();
			// 根据类获取其中的字段
			Field[] fields = clazz.getDeclaredFields();

			// 拼接 insert 语句中的字段名
			StringBuilder ff = new StringBuilder();
			// 获取各个字段具体的值
			List<Object> vv = new ArrayList<Object>();

			// 循环字段，获取字段信息并保存
			for (Field field : fields) {
				field.setAccessible(true);
				if (field.get(t) != null && !field.getType().toString().endsWith("List")) {
					ff.append("," + field.getName());
					vv.add(field.get(t));
				}
			}
			// 去掉多余逗号，加上括号
			ff = new StringBuilder(" (").append(ff.substring(1)).append(") ");

			StringBuilder sql = new StringBuilder("insert into `" + clazz.getSimpleName().toLowerCase() + "`" + ff + "values (");

			for (int i = 0; i < vv.size(); i++) {
				sql.append("?,");
			}

			sql = new StringBuilder(sql.substring(0, sql.length() - 1)).append(")");

			Connection conn = cpds.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());

			for (int i = 0; i < vv.size(); i++) {
				pstmt.setObject(i + 1, vv.get(i));
			}
			
			int update = pstmt.executeUpdate();
			conn.close();
			return update;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;

	}

	public static <T> int update(T t) {

		try {
			// 根据传入对象获取相应类
			Class<?> clazz = t.getClass();
			// 根据类获取其中的字段
			Field[] fields = clazz.getDeclaredFields();

			// 获取字段的 key-value 对
			Map<String, Object> fv = new HashMap<String, Object>();

			int id = 0;
			// 循环字段，获取字段信息并保存
			for (Field field : fields) {

				field.setAccessible(true);
				if (field.get(t) != null && !field.getType().toString().endsWith("List")) {
					fv.put(field.getName(), field.get(t));
					if (field.getName().equalsIgnoreCase("id")) {
						id = Integer.valueOf(field.get(t).toString());
					}
				}
			}

			// update语句的 set 段
			StringBuilder setStr = new StringBuilder();
			for (String t3 : fv.keySet()) {
				setStr.append(", " + t3 + " = ? ");
			}

			StringBuilder sql = new StringBuilder("update `" + clazz.getSimpleName() + "` set ").append(setStr.substring(2) + " where id = ?");

			Connection conn = cpds.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());

			int temp = 0;
			for (String t3 : fv.keySet()) {
				temp++;
				pstmt.setObject(temp, fv.get(t3));
			}
			pstmt.setObject(fv.size() + 1, id);

			int update = pstmt.executeUpdate();
			conn.close();
			return update;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

}
