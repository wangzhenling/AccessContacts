package com.example.accesscontacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class MainActivity extends Activity {

	private Button btnAdd,btnShow;
	private EditText etName,etPhone;
	private LinearLayout title;
	private ListView result;
	private ContentResolver resolver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		etName=(EditText) this.findViewById(R.id.etName);
		etPhone=(EditText) this.findViewById(R.id.etPhone);
		title=(LinearLayout) this.findViewById(R.id.title);
		result=(ListView) this.findViewById(R.id.result);
		btnAdd=(Button) this.findViewById(R.id.btnAdd);
		btnShow=(Button) this.findViewById(R.id.btnShow);
		
		title.setVisibility(View.INVISIBLE);
		resolver = getContentResolver();

		MyOnClickListener myOnClickListener = new MyOnClickListener();
		btnAdd.setOnClickListener(myOnClickListener);
		btnShow.setOnClickListener(myOnClickListener);

	}
	
	private class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) {
			case R.id.btnAdd:
				addPerson();
				break;
			case R.id.btnShow:
				title.setVisibility(View.VISIBLE);
				ArrayList<Map<String,String>> persons=queryPerson();
				SimpleAdapter adapter=new SimpleAdapter(MainActivity.this,persons, R.layout.result, new String[]{
						"id","name","num"}, new int[]{R.id.user_id,R.id.user_name,R.id.user_phone});
				result.setAdapter(adapter);
				break;
			default:
				break;
			}
		}
	
	}
	
	// 添加联系人
	public void addPerson(){
		
		String nameStr=etName.getText().toString();// 获取联系人姓名
		String numStr=etPhone.getText().toString();//获取联系人号码
		ContentValues values=new ContentValues();//创建一个空的ContentValues
		Uri rawContactUri = resolver.insert(RawContacts.CONTENT_URI, values);// 向RawContacts.CONTENT_URI执行一个空值插入，目的是获取返回的ID号。
		long contactId=ContentUris.parseId(rawContactUri);//得到新联系人的ID号
		values.clear();//清空values的内容
		values.put(Data.RAW_CONTACT_ID, contactId);//设置ID号
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);//设置类型
		values.put(StructuredName.GIVEN_NAME, nameStr);//设置姓名
		resolver.insert(android.provider.ContactsContract.Data.CONTENT_URI, values);//向联系人URI添加联系人姓名
		values.clear();
		values.put(Data.RAW_CONTACT_ID, contactId);//设置ID号
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);//设置类型
		values.put(Phone.NUMBER, numStr);//设置号码
		values.put(Phone.TYPE, Phone.TYPE_MOBILE);//设置电话类型
		resolver.insert(android.provider.ContactsContract.Data.CONTENT_URI, values);//向联系人电话号码URI添加电话号码
		Toast.makeText(MainActivity.this, "联系人数据添加成功！",1000).show();
		
	}
	
	public ArrayList<Map<String,String>> queryPerson(){
		//创建一个保存所有联系人信息的列表，每项是一个map对象
		ArrayList<Map<String,String>>detail=new ArrayList<Map<String,String>>();
		//查询通讯录中的所有联系人
		Cursor cursor=resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		//循环遍历每一个联系人
		while(cursor.moveToNext()){
			//每个联系人信息用一个map对象存储
			Map<String,String>person=new HashMap<String,String>();
			//获取联系人ID号
			String personId=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			//获取联系人姓名
			String name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			person.put("id", personId);//将获取到的信息存入map
			person.put("name", name);
			//工具ID号，查询手机号码
			Cursor nums=resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+personId, null, null);
			if(nums.moveToNext()){
				
				String num=nums.getString(nums.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				person.put("num", num);//将手机号存入map对象
			}
			nums.close();//关闭资源
			detail.add(person);
		}
		cursor.close();//关闭资源
		return detail;//返回查询列表	
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
