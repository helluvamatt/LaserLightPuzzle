package com.schneenet.android.lasers.levels;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.schneenet.android.lasers.R;

public class LevelAdapter extends BaseAdapter {

	public LevelAdapter(Context ctxt) {
		levels = new ArrayList<LaserLightPuzzleLevel>();
		mContext = ctxt;
	}
	
	public void add(LaserLightPuzzleLevel newLevel) {
		levels.add(newLevel);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return levels.size();
	}

	@Override
	public Object getItem(int position) {
		return get(position);
	}
	
	public LaserLightPuzzleLevel get(int position) {
		return levels.get(position);
	}

	@Override
	public long getItemId(int position) {
		// Uhh, not used.
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		// Re-use old view
		if (convertView != null && convertView instanceof LinearLayout) {
			return convertView;
		}
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.level_list_item, viewGroup, false);
		
		LaserLightPuzzleLevel level = get(position);
		((TextView) v.findViewById(R.id.level_list_item_name)).setText(level.getName());
		((TextView) v.findViewById(R.id.level_list_item_author)).setText(level.getAuthor());
		int res;
		switch (level.getDifficulty()) {
		case 3:
			res = R.drawable.difficulty_3;
			break;
		case 2:
			res = R.drawable.difficulty_2;
			break;
		case 1:
		default:	
			res = R.drawable.difficulty_1;
			break;
		}
		((ImageView) v.findViewById(R.id.level_list_item_difficulty_image)).setImageResource(res);
		return v;
	}
	
	private ArrayList<LaserLightPuzzleLevel> levels;
	private Context mContext;
	
}
