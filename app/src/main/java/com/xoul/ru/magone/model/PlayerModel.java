package com.xoul.ru.magone.model;

import com.xoul.ru.magone.model.effects.BurningEffect;
import com.xoul.ru.magone.model.effects.DeathEffect;
import com.xoul.ru.magone.model.effects.HealEffect;
import com.xoul.ru.magone.model.effects.WetEffect;
import com.xoul.ru.magone.model.entitys.DoubleDamageEntity;
import com.xoul.ru.magone.model.entitys.EndOfTurnEffectEntity;
import com.xoul.ru.magone.model.entitys.HalfDamageEntity;
import com.xoul.ru.magone.model.entitys.PermanentEffectEntity;
import com.xoul.ru.magone.model.spells.Spell;
import com.xoul.ru.magone.model.spells.SpellFactory;
import com.xoul.ru.magone.model.spells.SpellStorage;
import com.xoul.ru.magone.model.spells.SpellType;

import java.util.Iterator;
import java.util.List;

public class PlayerModel {
    public List<Effect> currentEffects;
    public List<Rune> currentSpell;
    private Hero hero;
    private int mp;
    private int maxSpellLength;
    private Spell spell;
    private SpellStorage spellStorage;
    private EndOfTurnEffectEntity endOfTurnEffectEntity;
    private PermanentEffectEntity permanentEffectEntity;

    public PlayerModel(Hero hero, int mp, List<Rune> currentSpell, List<Effect> currentEffects, SpellStorage spellStorage) {
        this.hero = hero;
        this.mp = mp;
        this.currentSpell = currentSpell;
        this.currentEffects = currentEffects;
        this.spellStorage = spellStorage;
    }

    public void addMP() {
        mp += hero.getMpcharge();
        if (mp > 10) mp = 10;
    }

    public int getHp() {
        return hero.getCurrenthp();
    }

    public int getMaxHp() {
        return hero.getMAXHP();
    }

    public int getMp() {
        return mp;
    }

    //Приводит в действие переданное заклинание, проверяя его тип и изменяя в соответсвии с имющимися эффектами
    public void setSpell(Spell spell) {
        if (spell.spellType == SpellType.Damage) {
            if (!spell.target.currentEffects.isEmpty())
                for (Effect eff : spell.target.currentEffects) {
                    eff.damage(spell.damage);
                    if (eff.isOpposite(spell.effectType))
                        spell.findOppositEffect();//проверяем вешать ли еффект
                }
            //наносим урон цели
            if (permanentEffectEntity != null)
                if (permanentEffectEntity instanceof DoubleDamageEntity)
                    permanentEffectEntity.effect(spell.damage, spell.target);
            spell.target.damage(spell.damage);
        }
        if (spell.spellType == SpellType.Heal) {
            if (!spell.target.currentEffects.isEmpty())
                for (Effect eff : spell.target.currentEffects) {
                    eff.heal(spell.heal);
                    if (eff.isOpposite(spell.effectType))
                        spell.findOppositEffect();//проверяем вешать ли еффект
                }
            //лечим цель
            spell.target.heal(spell.heal);
        }
        if (spell.spellType == SpellType.Buff) {

        }
        if (spell.isSettingEffect() && spell.effectType != null)
            spell.target.addEffect(spell.effectType);
        mp -= spell.manaAmountToCut;
        clearEffects();
    }

    //Наносит целочисленный урон игроку
    public void damage(Damage dmg) {
        if (permanentEffectEntity != null)
            if (permanentEffectEntity instanceof HalfDamageEntity)
                permanentEffectEntity.effect(dmg, this);
        hero.damage(dmg);
    }

    //Добавляет игроку какое-то целочисленное количество жизней
    public void heal(Heal heal) {
        hero.heal(heal);
    }

    //Добавляет руну по которой кликнули в текущий лист хранящий набранное заклинание
    public void addRuneToCurrentSpell(Rune rune) {
        currentSpell.add(rune);
    }

    //Метод написанный благодаря тому что саня педик
    public void addRuneToCurrentSpell(List<Rune> list) {
        currentSpell.clear();
        currentSpell = list;
    }

    //добавляет к текущим эффектам висящим на игроке новый переданного типа
    public void addEffect(EffectType effectType) {
        Iterator<Effect> effectIterator = currentEffects.iterator();
        while (effectIterator.hasNext()) {
            Effect eff = effectIterator.next();
            if (eff.type == effectType)
                effectIterator.remove();
        }
        Effect effect = null;
        if (effectType == EffectType.FIRE) {
            effect = new BurningEffect(Constants.BURNINGTIME, true, effectType, 0, 2);
        }
        if (effectType == EffectType.DEATH) {
            effect = new DeathEffect(Constants.DEATHTIME, true, effectType, 0, 2);
        }
        if (effectType == EffectType.HEAL) {
            effect = new HealEffect(Constants.HEALTIME, true, effectType, 2, 0);
        }
        if (effectType == EffectType.WET) {
            effect = new WetEffect(Constants.WETTIME, true, effectType, 0, 0);
        }
        if (effect != null)
            currentEffects.add(effect);
    }

    //очищает текущий лист заклинаний
    public void clearCurrenSpell() {
        currentSpell.clear();
    }

    //очищает эффекты у которых либо закончилось время действия либо которые были сняты другим эффектом
    public void clearEffects() {
        Iterator<Effect> eff = currentEffects.iterator();
        while (eff.hasNext()) {
            Effect effect = eff.next();
            if (!effect.isAvailable()) eff.remove();
        }
    }

    //для каждого эффекта из текущих вызывает метод оповещающий эффекты о конце текущего хода
    public void endOfTurn(PlayerModel currentPlayer) {
        if (endOfTurnEffectEntity != null)
            endOfTurnEffectEntity.effect(hero, this);
        for (Effect eff : currentEffects) {
            eff.endOfTurn(currentPlayer);
        }
    }

    //собирает заклинание из уже переданных
    public Spell createSpell(PlayerModel currentPlayer, PlayerModel enemy) {
        spell = SpellFactory.create(currentSpell, currentPlayer, enemy, spellStorage);
        return spell;
    }

    public boolean hasSpellATarget() {
        return spell.hasTarget();
    }

    public void setSpellTarget(PlayerModel target) {
        spell.setTarget(target);
    }

    public void endTurnEffect(int heal, int damage) {
        heal(new Heal(heal));
        damage(new Damage(damage, null));
    }
}
