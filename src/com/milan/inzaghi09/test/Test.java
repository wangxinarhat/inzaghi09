package com.milan.inzaghi09.test;

import java.util.Random;

import com.milan.inzaghi09.db.dao.BlacklistDao;

import android.test.AndroidTestCase;

public class Test extends AndroidTestCase {
	public void insert() {
		BlacklistDao dao = BlacklistDao.getInstance(getContext());
		for (int i = 0; i < 100; i++) {
			
			if (i<10) {
				
				dao.insert("1381688000"+i,new Random().nextInt(3)+1+"");
			} else {
				dao.insert("138168800"+i,new Random().nextInt(3)+1+"");

			}
		}
		
	}

	public void delete() {
		BlacklistDao dao = BlacklistDao.getInstance(getContext());
		dao.delete("138");
	}

	public void update() {
		BlacklistDao dao = BlacklistDao.getInstance(getContext());
		dao.update("138", 3 + "");
	}
	public void queryAll() {
		BlacklistDao dao = BlacklistDao.getInstance(getContext());
		dao.queryAll();
	}

}
