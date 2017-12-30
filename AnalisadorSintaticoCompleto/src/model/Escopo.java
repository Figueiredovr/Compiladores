package model;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Victor Figueiredo
 */
public class Escopo {


  public Escopo pai;
  public List variaveis =  new ArrayList();
  public List metodos =  new ArrayList();
  public List classes = new ArrayList();

  public Escopo (){}

  public Escopo (Escopo anterior){
    this.pai = anterior;

  }



}
