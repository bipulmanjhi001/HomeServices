package homeservice.com.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Spinner;

@SuppressLint("AppCompatCustomView")
public class MySpinner extends Spinner {

    private Context _context;

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
    }

    public MySpinner(Context context) {
        super(context);
        _context = context;
    }

    public MySpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context);
        _context = context;
    }

    @Override
    public View getChildAt(int index) {
        View v = new View(_context);
        return v;
    }

}