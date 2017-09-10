package com.example.android.gloryinventory;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.gloryinventory.data.ProductContract;


/**
 * Created by user on 9/5/2017.
 */
public class ProductCursorAdapter extends CursorAdapter {
    public static final String LOG_TAG = ProductCursorAdapter.class.getSimpleName();

    LayoutInflater mInflater = LayoutInflater.from(mContext);
    ContentResolver cr = mContext.getContentResolver();
    Cursor cur = cr.query(ProductContract.ProductEntry.CONTENT_URI, null, null, null, null);

    public ProductCursorAdapter(Context context, Cursor cur) {
        super(context, cur, 0 /* flags */);
        this.mContext = (Activity) context;
        mInflater = LayoutInflater.from(context);
        this.cur = cur;
//   cur.moveToFirst();
       // initializeChecked();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {


            convertView = this.mInflater.inflate(R.layout.list_item, null);
            viewHolder = new ViewHolder();
            //.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)
            viewHolder.nameView = (TextView) convertView.findViewById(R.id.name);
            viewHolder.quantityView = (TextView) convertView.findViewById(R.id.quantity);
            viewHolder.priceView = (TextView) convertView.findViewById(R.id.price);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.product_img);
            viewHolder.sellButton = (ImageButton) convertView.findViewById(R.id.sell_btn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String temp = Integer.toString(position);
        Log.e("position " , temp);
        if ((cur!=null ) && (cur.moveToFirst())){
            cur.moveToPosition(position);
        final String name = cur.getString(cur.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME));
        final int quantity = cur.getInt(cur.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));
        final float price = cur.getFloat(cur.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE));
        final String imagePath = cur.getString(cur.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE));
        viewHolder.nameView.setText(name);
        viewHolder.quantityView.setText(Integer.toString(quantity));
        viewHolder.priceView.setText(Float.toString(price));

        //viewHolder.sellButton.setText(this.cur.getString(this.cur.getColumnIndex(MyDBHelper.COUNTRY)));
        //Checking image view , so the ImageView isn't blank if the image doesn't exist.
        ImageView imageView = (ImageView) convertView.findViewById(R.id.product_img);
        ImageButton sellButton = convertView.findViewById(R.id.sell_btn);
        if (imagePath != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(Uri.parse(imagePath));
        } else {
            imageView.setVisibility(View.GONE);
        }

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 0) {
                    Object obj = view.getTag();
                    String string = obj.toString();
                    //listView.getAdapter().getItemId(position);
                    // SQLiteDatabase database = mDbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, name);
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity - 1);
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, price);
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE, imagePath);
                    //Uri currentProductUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, Integer.parseInt(st));
                    Uri currentProductUri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, Integer.parseInt(string));
                    int rowsUpdated = mContext.getContentResolver().update(currentProductUri, values, null, null);
                    if (rowsUpdated == -1) {
                        Log.e(LOG_TAG, "Failed to update row for " + currentProductUri);

                    }
                    // given URI has changed
                    if (rowsUpdated != 0) {
                        mContext.getContentResolver().notifyChange(currentProductUri, null);
                    }


                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.not_in_stock), Toast.LENGTH_LONG).show();
                }
            }
        });
        //sellButton.setTag(obj);

    }
        return convertView;}




    static class ViewHolder {
        TextView nameView;
        TextView quantityView;
        TextView priceView;
        ImageView imageView;
        ImageButton sellButton;
    }
}