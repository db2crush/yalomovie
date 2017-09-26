package org.artoolkit.ar.samples.ARNative;

import android.widget.FrameLayout;

import com.threed.jpct.World;

import org.artoolkit.ar.jpct.ArJpctActivity;
import org.artoolkit.ar.jpct.TrackableObject3d;

import java.util.List;

/**
 * Created by erunn on 2017-09-26.
 */

public class AstroActivity extends ArJpctActivity {
    @Override
    protected void populateTrackableObjects(List<TrackableObject3d> list) {
        
    }

    @Override
    public void configureWorld(World world) {

    }

    @Override
    protected FrameLayout supplyFrameLayout() {
        return null;
    }
}
