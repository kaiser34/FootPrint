package com.Activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import com.Adapter.ActivityChooseFocusListAdapter;
import com.Adapter.GridAdapter;
import com.Bll.MainBindService;
import com.Bll.MinaSocket;
import com.Bll.PictureSocket;
import com.Common.FileUploadRequest;
import com.Tool.Bimp;
import com.Tool.FoucsType;
import com.Tool.ImageItem;
import com.Tool.SPUtils;
import com.Tool.ToastUtil;
import com.muli_image_selector.onee.MultiImageSelector;
import com.muli_image_selector.onee.MultiImageSelectorActivity;
import Entity.FocusEntity;
import Entity.SetFocusBriefListEntity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ActivityCreateFocus extends Activity{
	
	private static final int REQUEST_IMAGE = 2;
	private static final int PREVIEW=1;
	
	private TextView submit,nowaddress,focustype;
	private ImageView back;
	private EditText title,content,style;
	private GridView gridview;
	private GridAdapter adapter;
	
	private List<FoucsType>list=new ArrayList<FoucsType>();
	//private SetFocusBriefListEntity focuslist;
	private List<FoucsType>foucslist=new ArrayList<FoucsType>();
	private String FoucsType;
	private int bigfoucsid;//大关注点id
	private String bigfoucsname;//大关注点名称
	
	//位置信息
	private String city,address;
	private double mLatitude,mLongtitude;
	
	private ArrayList<String> path;
	
	public static Handler mhander;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_createfocus);
		Intent intent=getIntent();
		mLatitude=intent.getDoubleExtra("Latitude",-1);
		mLongtitude=intent.getDoubleExtra("Longtitude", -1);
		city=intent.getStringExtra("City");
		address=intent.getStringExtra("Address");
		try {
			MinaSocket.SendMessage(new JSONObject().put("tag",3));
			JSONObject json=new JSONObject();
			json.put("tag", 44);
			json.put("page", 1);
			json.put("city", city);
			MinaSocket.SendMessage(json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updateUI();
		initView();
		setlisten();
		
		gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		gridview.setAdapter(adapter);
	}
	
	
	private void updateUI() {
		// TODO Auto-generated method stub
		mhander=new Handler(){
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==0){
					if(MainBindService.FoucsType.isFlag()){
						list=(List<FoucsType>) MainBindService.FoucsType.getData();
					}else{
						ToastUtil.show(ActivityCreateFocus.this, "数据获取异常");
					}
				}else if(msg.what==1){
					if((Boolean) msg.obj){
						ToastUtil.show(ActivityCreateFocus.this,"发送成功");
						finish();
					}else{
						ToastUtil.show(ActivityCreateFocus.this,"发送失败");
					}
				}else if(msg.what==2){
					if(MainBindService.sCityToFocusList.isFlag()){
						
						SetFocusBriefListEntity focuslist= (SetFocusBriefListEntity) MainBindService.sCityToFocusList.getData();
						for(int i=0;i<focuslist.getSize();i++){
							foucslist.add(new FoucsType(focuslist.getItem(i).getFocus_id(), focuslist.getItem(i).getFocus_title()));
						}
					}else{
						ToastUtil.show(ActivityCreateFocus.this,"数据获取异常");
					}
				}
			}
		};
	}

	private void initView() {

		submit=(TextView)findViewById(R.id.create_text);
		nowaddress=(TextView) findViewById(R.id.nowaddress);
		title=(EditText) findViewById(R.id.focustitle);
		content=(EditText) findViewById(R.id.focusinfo);
		style=(EditText) findViewById(R.id.focusstyle);
//		if(!address.isEmpty()){
//		nowaddress.setText(address);
//		}else{
//			nowaddress.setText("地址获取失败");
//		}
		focustype=(TextView) findViewById(R.id.focustype);
		back=(ImageView)findViewById(R.id.back_image);
		gridview=(GridView) findViewById(R.id.noScrollgridview);
	}
	
	private void setlisten() {
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		nowaddress.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (!foucslist.isEmpty()) {
						
						String[] arrayContestLevel = new String[foucslist.size()];
						for(int i=0;i<foucslist.size();i++){
							arrayContestLevel[i]=foucslist.get(i).getName();
						}
						
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(
								ActivityCreateFocus.this)
						        .setTitle("请选择")
								.setItems(arrayContestLevel,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog,
													int which) { 
												nowaddress.setText(foucslist.get(which).getName());
												nowaddress.setTextColor(Color.BLACK);
												bigfoucsid=foucslist.get(which).getId();
												bigfoucsname=foucslist.get(which).getName();
												dialog.cancel();
											} 
										});
						alertDialog.create().show();
					}else{
						nowaddress.setText("无上级关注点可选");
					}
				}
				return true;
			}
			
			
		});
		focustype.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (!list.isEmpty()) {
						
						String[] arrayContestLevel = new String[list.size()];
						for(int i=0;i<list.size();i++){
							arrayContestLevel[i]=list.get(i).getName();
						}
						
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(
								ActivityCreateFocus.this)
						        .setTitle("请选择")
								.setItems(arrayContestLevel,
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog,
													int which) { 
												focustype.setText(list.get(which).getName());
												FoucsType=list.get(which).getName();
												dialog.cancel();
											}
										});
						alertDialog.create().show();
					}else{
						focustype.setText("无类型可选");
					}
				}
				return true;
			}
		});
		
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
        		try {
					List<FileUploadRequest> l=getFileUploadRequest(path);
					PictureSocket.SendMessage(l);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
				//System.out.println(imageaddr);
				
				
				
//				if(title.length()>0){
//					if(content.length()>14){
//						if(style.length()>0){
//							FocusEntity entity=new FocusEntity();
//							entity.setFocusAddress(address);
//							entity.setFocusCity(city);
//							entity.setFocusCreateUserName((String)SPUtils.get(ActivityCreateFocus.this, "userNickname", ""));
//							entity.setFocusCreateUserId((Integer) SPUtils.get(ActivityCreateFocus.this,"userId", -1));
//							entity.setFocusLatitude(mLatitude);
//							entity.setFocusLongitude(mLongtitude);
//							entity.setFocusStyle(style.getText().toString());
//							entity.setFocusTitle(title.getText().toString());
//							entity.setFocusType(FoucsType);
//							entity.setFoucsContent(content.getText().toString());
//							entity.setTopFoucs(bigfoucsid);
//							entity.setTopFoucsname(bigfoucsname);
//							
//							JSONObject json=new JSONObject();
//							try {
//								json=new FocusEntity().ToJSON(2, entity);
//								MinaSocket.SendMessage(json);
//							} catch (JSONException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							
//						}else{
//							ToastUtil.show(ActivityCreateFocus.this, "请输入风格");
//						}
//					}else{
//						ToastUtil.show(ActivityCreateFocus.this, "内容不得少于15字");
//					}
//				}else{
//					ToastUtil.show(ActivityCreateFocus.this, "请输入标题");
//				}
			}
		});
		
		gridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == Bimp.tempSelectBitmap.size()) {
					MultiImageSelector.create(ActivityCreateFocus.this).origin(path)
			        .start(ActivityCreateFocus.this, REQUEST_IMAGE);
					
				} else {
					Intent intent = new Intent(ActivityCreateFocus.this,
							ActivityPreview.class);
					intent.putExtra("position",arg2);
					intent.putStringArrayListExtra("path", path);
					startActivityForResult(intent,PREVIEW);
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_IMAGE){
	        if(Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK){
	            // 获取返回的图片列表
	        	Bimp.tempSelectBitmap.clear();
	            path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
	           for(int i=0;i<path.size();i++){
	        	   ImageItem takePhoto = new ImageItem();
  					takePhoto.setImagePath(path.get(i));
  					Bimp.tempSelectBitmap.add(takePhoto);
	           }
	           adapter.notifyDataSetChanged();
	        }
	    }
		if(requestCode == PREVIEW){
			if(resultCode==RESULT_OK){
				path=data.getStringArrayListExtra("path");
			}
		}
	}
	
	 private static List<FileUploadRequest> getFileUploadRequest(ArrayList<String> path2) throws Exception  
	    {  
	    	List<FileUploadRequest> fileList = new ArrayList<FileUploadRequest>();   //定义图片list
	    	
	    	String []fileSplit = (String[])path2.toArray(new String[path2.size()]);
	    	System.out.println(fileSplit.length);
	    	
	    	for (int i = 0; i < fileSplit.length; i++) {
				
	    		FileUploadRequest fileUploadRequest = new FileUploadRequest();
	    		FileInputStream fileInputStream = new FileInputStream(fileSplit[i]);           //文件
				FileChannel channel=fileInputStream.getChannel();
				ByteBuffer bytebuffer=ByteBuffer.allocate((int) channel.size());
				bytebuffer.clear();
				channel.read(bytebuffer);          //将图片转换为字节`
				fileUploadRequest.setImagelongth(channel.size());       //图片长度
				fileUploadRequest.setBytes(bytebuffer.array());         //图片字节数组
				fileUploadRequest.setAlonght((int) (4+8+4+4+fileUploadRequest.getImagelongth()));          //数据流总长度：alonght+图片长度+tagPage+number+图片字节
				System.out.println("limit:"+bytebuffer.limit());
				System.out.println("fileUploadRequest.getImagelongth():"+fileUploadRequest.getImagelongth());
				
				channel.close();
				fileInputStream.close();
		        fileList.add(fileUploadRequest);
	    	}
	          
	        return fileList;  
	    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		adapter.notifyDataSetChanged();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Bimp.tempSelectBitmap.clear();
		
	}
	
}
