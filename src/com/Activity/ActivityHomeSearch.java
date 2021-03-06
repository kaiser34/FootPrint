package com.Activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.Adapter.FoucsBreifListAdapter;
import com.Adapter.FragmentTripPageListAdapter;
import com.Bll.MainBindService;
import com.Bll.MinaSocket;
import com.Tool.ToastUtil;

import Entity.FocusBriefEntity;
import Entity.SetFocusBriefListEntity;
import Entity.SetTripBriefListEntity;
import Entity.TripBriefEntity;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class ActivityHomeSearch extends Activity{

	private EditText search_edit;
	private TextView sure;
	private ImageView back;
	private Spinner searchtype;
	private ListView listview;
	
	private SetFocusBriefListEntity foucslist;
	private SetTripBriefListEntity triplist;
	private FoucsBreifListAdapter foucsadapter;
	private FragmentTripPageListAdapter tripadapter;
	
	private int type;
	private String keyword;
	
	public static Handler mhander;
	
	//刷新数据变量
		private View footview;
		private TextView tv_tip;
		private LinearLayout vis;
		private int i=1;
		private int visibleLastIndex;   //用来可显示的最后一条数据的索引值
		private ProgressBar progressBar;
		private boolean moredata = true;
		private boolean firstdata = true;
		private LayoutInflater inflater;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.activity_homesearch);
		
		inflater = LayoutInflater.from(this);
		initView();
		setlisten();
		
		updateUI();
		
	}

	private void updateUI() {
		// TODO Auto-generated method stub
		mhander=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==0){
					if(MainBindService.sSearchList.isFlag()){
						int flag=(Integer) MainBindService.sSearchList.getData();
						if(flag==0){
							if(firstdata){
								foucslist=(SetFocusBriefListEntity) MainBindService.sSearchList.getData1();
								if(foucslist.getSize()<15){
									vis.setVisibility(View.GONE);
								}else{
									vis.setVisibility(View.VISIBLE);
								}
								foucsadapter = new FoucsBreifListAdapter(ActivityHomeSearch.this, foucslist);
								listview.addFooterView(footview);
								listview.setAdapter(foucsadapter);
								listview.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										// TODO Auto-generated method stub
										Intent intent=new Intent(ActivityHomeSearch.this,ActivityFoucsDetail.class);
										intent.putExtra("foucsid",foucslist.getItem(position).getFocus_id());
										startActivity(intent);
									}
								});
								firstdata=false;
								}else{
									List<FocusBriefEntity>l=new ArrayList<FocusBriefEntity>();
									List<FocusBriefEntity>nl=new ArrayList<FocusBriefEntity>();
									
									nl=((SetFocusBriefListEntity) MainBindService.sSearchList.getData1()).getFocusBriefList();
									if(!nl.isEmpty()){
									l=foucslist.getFocusBriefList();
									l.addAll(nl);
									foucslist=new SetFocusBriefListEntity(l);
									foucsadapter.notifyDataSetChanged();
									}else{
										progressBar.setVisibility(View.GONE);
										tv_tip.setText("没有更多");
										moredata=false;
									}
								}
							
						}else if(flag==1){
							if(firstdata){
								triplist=(SetTripBriefListEntity) MainBindService.sSearchList.getData1();
								if(triplist.getSize()<15){
									vis.setVisibility(View.GONE);
								}else{
									vis.setVisibility(View.VISIBLE);
								}
								tripadapter = new FragmentTripPageListAdapter(ActivityHomeSearch.this, triplist);
								listview.addFooterView(footview);
								listview.setAdapter(tripadapter);
								listview.setOnItemClickListener(new OnItemClickListener() {

									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										// TODO Auto-generated method stub
										Intent intent=new Intent(ActivityHomeSearch.this,ActivityTripDetail.class);
										intent.putExtra("tripid",triplist.getItem(position).getTrip_id());
										startActivity(intent);
									}
								});
								firstdata=false;
								}else{
									List<TripBriefEntity>l=new ArrayList<TripBriefEntity>();
									List<TripBriefEntity>nl=new ArrayList<TripBriefEntity>();
									
									nl=((SetTripBriefListEntity) MainBindService.sSearchList.getData1()).getTripBriefList();
									if(!nl.isEmpty()){
									l=triplist.getTripBriefList();
									l.addAll(nl);
									triplist=new SetTripBriefListEntity(l);
									tripadapter.notifyDataSetChanged();
									}else{
										progressBar.setVisibility(View.GONE);
										tv_tip.setText("没有更多");
										moredata=false;
									}
								}
						
						}else if(flag==-1){
							ToastUtil.show(ActivityHomeSearch.this,"无搜索结果");
						}
					}
				}
			}
		};
	}

	private void initView() {
		// TODO Auto-generated method stub
		search_edit=(EditText)findViewById(R.id.homesearch_edit);
		sure=(TextView)findViewById(R.id.search);
		back=(ImageView)findViewById(R.id.back_image);
		searchtype=(Spinner)findViewById(R.id.searchtype);
		listview=(ListView) findViewById(R.id.listview);
		
		footview=inflater.inflate(R.layout.loading, null);
		tv_tip=(TextView) footview.findViewById(R.id.textView1);
		progressBar=(ProgressBar) footview.findViewById(R.id.progressBar1);
		vis=(LinearLayout) footview.findViewById(R.id.vis);
		
		
		
	}
	private void setlisten() {
		// TODO Auto-generated method stub
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		searchtype.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				type=arg2;
				ToastUtil.show(ActivityHomeSearch.this,searchtype.getItemAtPosition(arg2).toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		sure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(search_edit.length()>0){
					keyword=search_edit.getText().toString();
					JSONObject json=new JSONObject();
					
					try {
						json.put("tag",39);
						json.put("searchTag", type);
						json.put("searchName", keyword);
						json.put("page", 1);
						MinaSocket.SendMessage(json);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					ToastUtil.show(ActivityHomeSearch.this,"关键字不能为空");
				}
				listview.setAdapter(null);
				listview.removeFooterView(footview);
				i=1;
				moredata = true;
				firstdata = true;
			}
		});
		listview.setOnScrollListener(new OnScrollListener() {  //用来监听当list滑到最下面的时候的监听事件
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == SCROLL_STATE_IDLE && moredata &&
						listview.getAdapter().getCount() == visibleLastIndex){
					i=i+1;	
					requestServer(i);
					
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				visibleLastIndex = firstVisibleItem + visibleItemCount-1;
			}
		});
	}
	
	private void requestServer(int i) {
		// TODO Auto-generated method stub
		try {
			JSONObject json= new JSONObject();
			json.put("tag",39);
			json.put("searchTag", type);
			json.put("searchName", keyword);
			json.put("page", i);
			MinaSocket.SendMessage(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
