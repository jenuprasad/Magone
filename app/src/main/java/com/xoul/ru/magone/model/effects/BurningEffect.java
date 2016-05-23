package com.xoul.ru.magone.model.effects;

import com.xoul.ru.magone.model.Damage;
import com.xoul.ru.magone.model.Effect;
import com.xoul.ru.magone.model.EffectType;

public class BurningEffect extends Effect {


    public BurningEffect(int timeleft, boolean available, EffectType type) {
        super(timeleft, available, type);
    }

    @Override
    public Damage damage(Damage damage) {
        if (damage.effectType == EffectType.WET)
            damage.damage -= 3;
        return damage;
    }

    @Override
    public boolean isOpposite(EffectType type) {
        if (type == EffectType.WET)
            return true;
        return false;
    }
}