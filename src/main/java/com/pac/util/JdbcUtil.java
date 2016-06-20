package com.pac.util;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.pac.model.Match;
import com.pac.model.OddsMap;

public class JdbcUtil {

	public static void main(String[] args) {
		getOddsMapsWithPage(1, 10);
	}

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
	public static List<Match> getMatchsForZoudi() {
		
		try {
			List<Match> matchs = new ArrayList<Match>();
			
			String sql = "SELECT * FROM `match` where leagueId in (36,4,2,31,11) and time>'2013-08-01 00:00:00'";
			
			// 获取连接
			Connection conn = cpds.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Match match = new Match();
				match.setId(rs.getInt("id"));
				matchs.add(match);
			}
			return matchs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//五大联赛 Eurobet 伟德 立博 威廉希尔 bet365 Oddset SB
	public static List<OddsMap> getOddsMaps5League() {
		try {

			List<OddsMap> oddsMaps = new ArrayList<OddsMap>();

			String sql = "SELECT * FROM `oddsmap` where matchId in (SELECT id FROM `match` where leagueId in (36,4,2,31,11)) and companyId in (71,81,82,115,281,370,545) order by id desc";

			// 获取连接
			Connection conn = cpds.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				OddsMap oddsMap = new OddsMap();
				oddsMap.setId(rs.getInt("id"));
				oddsMap.setMatchId(rs.getInt("matchId"));
				oddsMap.setCompanyId(rs.getInt("companyId"));
				oddsMaps.add(oddsMap);
			}
			return oddsMaps;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static List<OddsMap> getOddsMapsWithPage(int page, int pageSize) {
		try {
			List<OddsMap> oddsMaps = new ArrayList<OddsMap>();

			String sql = "select * from oddsmap ORDER BY id desc limit ?,?";

			// 获取连接
			Connection conn = cpds.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());

			pstmt.setInt(1, (page - 1) * pageSize);
			pstmt.setInt(2, pageSize);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				OddsMap oddsMap = new OddsMap();
				oddsMap.setId(rs.getInt("id"));
				oddsMap.setMatchId(rs.getInt("matchId"));
				oddsMap.setCompanyId(rs.getInt("companyId"));
				oddsMaps.add(oddsMap);
			}

			return oddsMaps;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
			// 获取各字段的类型
			List<String> tt = new ArrayList<String>();
			// 获取字段的 key-value 对
			Map<String, Object> fv = new LinkedHashMap<String, Object>();

			// 循环字段，获取字段信息并保存
			for (Field field : fields) {
				field.setAccessible(true);
				if (field.get(t) != null && !field.getType().toString().endsWith("List")) {
					ff.append("," + field.getName());
					vv.add(field.get(t));
					tt.add(field.getType().toString());
					fv.put(field.getName(), field.get(t));
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
