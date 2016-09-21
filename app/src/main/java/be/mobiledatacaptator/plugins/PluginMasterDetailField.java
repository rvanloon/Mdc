package be.mobiledatacaptator.plugins;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import be.mobiledatacaptator.R;

public class PluginMasterDetailField extends TableRow {

	public PluginMasterDetailField(Context context, final Fragment fragment) {
		super(context);
		Button button = new Button(getContext());
		button.setText(R.string.kiesVerbinding);
		button.setLayoutParams(new android.widget.TableRow.LayoutParams(1));
		addView(button);

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), PluginMasterDetail.class);
				intent.putExtra("masterField", "OpwaartseRef");
				intent.putExtra("detailField", "OpwaartseLetter");
				fragment.startActivityForResult(intent, PluginMasterDetail.PLUGIN_MASTER_DETAIL_ACTRESULT);
			}
		});
	}

	public PluginMasterDetailField(Context context) {
		super(context);
	}

}
