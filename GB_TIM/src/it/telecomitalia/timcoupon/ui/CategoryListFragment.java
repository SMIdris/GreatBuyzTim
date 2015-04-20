package it.telecomitalia.timcoupon.ui;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.R;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.onmobile.utils.AppConstants;
import com.onmobile.utils.Utils;
import com.squareup.picasso.Picasso;

public final class CategoryListFragment extends ListFragment
{
	final class Category
	{
		public Category(String name, String url)
		{
			this.name = name;
			this.url = url;
		}

		public String name;
		public String url;
	}

	OnCategoryClick dealItemClick;
	FirstPageFragmentListener fm;
	static Activity activity;

	public static CategoryListFragment newInstance(String content, Activity screen, OnCategoryClick dealItemClick,
			FirstPageFragmentListener f)
	{
		CategoryListFragment fragment = new CategoryListFragment();

		fragment.dealItemClick = dealItemClick;
		fragment.fm = f;
		activity = screen;
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		activity = getActivity();

		List<Category> list = new ArrayList<CategoryListFragment.Category>();
		Cursor c = GreatBuyzApplication.getDataController().getCategoriesCursor();
		boolean success = c.moveToNext();
		while (success)
		{
			String categoryName = c.getString(0);
			String url = c.getString(2);
			list.add(new Category(categoryName, url));
			success = c.moveToNext();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		List<Category> list = new ArrayList<CategoryListFragment.Category>();
		Cursor c = GreatBuyzApplication.getDataController().getCategoriesCursor();
		boolean success = c.moveToNext();
		while (success)
		{
			String categoryName = c.getString(0);
			String url = c.getString(2);
			list.add(new Category(categoryName, url));
			success = c.moveToNext();
		}
		setListAdapter(new CategoryAdapter(getActivity(), R.layout.categorylistitem, R.id.imgCategoryListItem, list));
	}

	@Override
	public void onStart()
	{
		super.onStart();
		// FlurryAgent.onPageView();
		//GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit("CategoryList");
		// we are taking page name from message table for language support
		String name = Utils.getMessageString(AppConstants.Messages.dealsByCategories, R.string.dealsByCategories);
		GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit(name);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		fm = ((SampleTabsStyled) activity).getCategoryFragmentNavigationListner();
		dealItemClick = (OnCategoryClick) activity;
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View listView = (View) inflater.inflate(R.layout.category_list_view, null);
		return listView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		dealItemClick.onCategoryClick(position, fm);
	}

	class CategoryAdapter extends ArrayAdapter<Category>
	{
		List<Category> data = null;
		Context context;
		int layout;
		int imageViewResourceId;
		Bitmap defaultImage;
		Bitmap overlay;
		LayoutInflater inflater;

		public CategoryAdapter(Context context, int layout, int imageViewResourceId, List<Category> categories)
		{
			super(context, layout, imageViewResourceId);

			inflater = activity.getLayoutInflater();

			this.imageViewResourceId = imageViewResourceId;
			this.context = context;
			this.layout = layout;
			this.data = categories;
			defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.default_category);
		}

		@Override
		public int getCount()
		{
			return data.size();
		}

		@Override
		public Category getItem(int position)
		{
			return data.get(position);
		}

		class ViewHolder
		{
			ImageView imgView;
			TextView txtCategoryName;

			public ViewHolder(View v)
			{
				imgView = (ImageView) v.findViewById(imageViewResourceId);
				txtCategoryName = (TextView) v.findViewById(R.id.txt_category_name);
				txtCategoryName.setTypeface(GreatBuyzApplication.getApplication().getFont());
			}
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			View row = convertView;
			ViewHolder holder = null;

			if (row == null)
			{
				row = inflater.inflate(layout, null);
				holder = new ViewHolder(row);
				row.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) row.getTag();
			}

			holder.imgView.setImageDrawable(new BitmapDrawable(defaultImage));

			String imgUrl = data.get(position).url;
			holder.imgView.setTag(imgUrl);
			if (!TextUtils.isEmpty(imgUrl))
				Picasso.with(activity).load(imgUrl).config(Bitmap.Config.RGB_565).fit().centerCrop().into(holder.imgView);
			holder.txtCategoryName.setText(data.get(position).name);
			return row;
		}
	}
}
