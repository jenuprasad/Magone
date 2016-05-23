package com.xoul.ru.magone.view.player.unit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.xoul.ru.magone.view.other.Utils;

public class UnitField extends LinearLayout {
    private static final int UNIT_WIDTH_DP = 100;
    private static final int UNIT_MARGIN_DP = 5;
    private LayoutParams layoutParams;

    private Unit[] units;

    public UnitField(Context context) {
        super(context);
        initLayout();
        initUnits(context);
    }

    public UnitField(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
        initUnits(context);
    }

    private void initLayout() {
        units = new Unit[Slot.values().length];
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        int width = Utils.dpPx(getContext(), UNIT_WIDTH_DP);
        int margin = Utils.dpPx(getContext(), UNIT_MARGIN_DP);
        layoutParams = new LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(margin, margin, margin, margin);
    }

    private void initUnits(Context context) {
        addUnit(new Unit(context), Slot.HERO);
    }

    public void addUnit(Unit unit, Slot slot) {
        if (units[slot.ordinal()] != null) {
            removeUnit(slot);
        }
        units[slot.ordinal()] = unit;
        removeAllUnitViews();
        for (Unit item : units) {
            if (item != null) {
                addView(item, layoutParams);
            }
        }
    }

    public void removeUnit(Slot slot) {
        removeView(units[slot.ordinal()]);
        units[slot.ordinal()] = null;
    }

    public void removeAllUnits() {
        for (int i = 0; i < units.length; i++) {
            if (units[i] != null) {
                removeView(units[i]);
                units[i] = null;
            }
        }
    }

    private void removeAllUnitViews() {
        for (Unit unit : units) {
            if (unit != null) {
                removeView(unit);
            }
        }
    }

    public enum Slot {
        WARRIOR,
        HERO,
        SUPPORT
    }
}
