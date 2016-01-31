package com.gamesmith.monsterboard.common.model;

/**
 * Created by clocksmith on 5/25/15.
 */
public class Player {
  private String name;
  private String monster;
  private Long hp;
  private Long vp;

  public Player(String name, String monster, Long hp, Long vp) {
    this.name = name;
    this.monster = monster;
    this.hp = hp;
    this.vp = vp;
  }

  public Player() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMonster() {
    return monster;
  }

  public void setMonster(String monster) {
    this.monster = monster;
  }

  public Long getHp() {
    return hp;
  }

  public void setHp(Long hp) {
    this.hp = hp;
  }

  public Long getVp() {
    return vp;
  }

  public void setVp(Long vp) {
    this.vp = vp;
  }
}