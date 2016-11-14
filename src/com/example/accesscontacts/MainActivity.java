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
	
	// �����ϵ��
	public void addPerson(){
		
		String nameStr=etName.getText().toString();// ��ȡ��ϵ������
		String numStr=etPhone.getText().toString();//��ȡ��ϵ�˺���
		ContentValues values=new ContentValues();//����һ���յ�ContentValues
		Uri rawContactUri = resolver.insert(RawContacts.CONTENT_URI, values);// ��RawContacts.CONTENT_URIִ��һ����ֵ���룬Ŀ���ǻ�ȡ���ص�ID�š�
		long contactId=ContentUris.parseId(rawContactUri);//�õ�����ϵ�˵�ID��
		values.clear();//���values������
		values.put(Data.RAW_CONTACT_ID, contactId);//����ID��
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);//��������
		values.put(StructuredName.GIVEN_NAME, nameStr);//��������
		resolver.insert(android.provider.ContactsContract.Data.CONTENT_URI, values);//����ϵ��URI�����ϵ������
		values.clear();
		values.put(Data.RAW_CONTACT_ID, contactId);//����ID��
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);//��������
		values.put(Phone.NUMBER, numStr);//���ú���
		values.put(Phone.TYPE, Phone.TYPE_MOBILE);//���õ绰����
		resolver.insert(android.provider.ContactsContract.Data.CONTENT_URI, values);//����ϵ�˵绰����URI��ӵ绰����
		Toast.makeText(MainActivity.this, "��ϵ��������ӳɹ���",1000).show();
		
	}
	
	public ArrayList<Map<String,String>> queryPerson(){
		//����һ������������ϵ����Ϣ���б�ÿ����һ��map����
		ArrayList<Map<String,String>>detail=new ArrayList<Map<String,String>>();
		//��ѯͨѶ¼�е�������ϵ��
		Cursor cursor=resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		//ѭ������ÿһ����ϵ��
		while(cursor.moveToNext()){
			//ÿ����ϵ����Ϣ��һ��map����洢
			Map<String,String>person=new HashMap<String,String>();
			//��ȡ��ϵ��ID��
			String personId=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			//��ȡ��ϵ������
			String name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			person.put("id", personId);//����ȡ������Ϣ����map
			person.put("name", name);
			//����ID�ţ���ѯ�ֻ�����
			Cursor nums=resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+personId, null, null);
			if(nums.moveToNext()){
				
				String num=nums.getString(nums.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				person.put("num", num);//���ֻ��Ŵ���map����
			}
			nums.close();//�ر���Դ
			detail.add(person);
		}
		cursor.close();//�ر���Դ
		return detail;//���ز�ѯ�б�	
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
