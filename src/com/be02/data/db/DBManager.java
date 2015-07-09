/**
 * @author tan_zhenq E-mail: tan_zhenqi@163.com
 * @date ����ʱ�䣺2015-7-9 ����10:20:03 
 * @version 1.0 
 * @parameter  
 * @since  
 * @return  
 */
package com.be02.data.db;

import java.util.ArrayList;
import java.util.List;

import com.be02.aidl.MusicItem;
import com.be02.data.MusicLog;
import com.be02.musicplayer.R;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

/**
 * @author lz100
 *
 */
public final class DBManager {

	public static DBManager getInstance(Context c)
	{
		if (mDBMgr == null) {
			mDBMgr = new DBManager(c);
			return mDBMgr;
		} 
		return mDBMgr;
	}
	
	
	public List<MusicItem> getMusicLisit()
	{
		synchronized (mMusicList) {
			return mMusicList;
		}
		
	}
	
	private DBManager(Context c)
	{
		mContext = c;
		initialize();
	}
	
	private void initialize()
	{
		mMusicList = new ArrayList<MusicItem>();
		updateList();
	}

	public void updateList()
	{
		new Thread(new Runnable() {
			public void run() {
				if (mContext == null) {
					MusicLog.e(SUB_TAG + "updateList mContext == null");
					return;
				}
				
				mContentResolver = mContext.getContentResolver();
				if (mContentResolver == null) {
					return;
				}
				Cursor cursor = mContentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
				if (cursor == null) {
					MusicLog.e(SUB_TAG + "updateList cursor == null");
					return;
				}
				cursor.moveToFirst();
				do {
					String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
					String airtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
					if (airtist.equals("<unknown>")) {
						airtist = mContext.getString(R.string.unKnowArtist);
					}
					String ablum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
					String dispName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
					String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
					int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
					int time = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
					int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
					if (isMusic != 0 && time > 1000) {
						MusicItem item = new MusicItem(title, airtist, ablum, dispName, uri, size, time);
						synchronized (mMusicList) {
							mMusicList.add(item);
						}
					}
					
				} while(cursor.moveToNext());
				cursor.close();
			}
		}).start();
		

	}
	
	private static DBManager mDBMgr = null;
	private ContentResolver mContentResolver;
	private List<MusicItem> mMusicList;
	private Context mContext;
	private final String SUB_TAG = DBManager.class.toString() + " ";
}