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

  public boolean add_variavel (Var nova){
    Var variavel;
    //verifica se a variavel a ser adicionada ja existe nesse escopo
    for (int i = 0 ; i< this.variaveis.size(); i++){
      variavel = (Var) this.variaveis.get(i);
      if (nova.nome_variavel.equals(variavel.var_nome)) {
        return false;
      }
    }

    this.variaveis.add(nova);
    return true;

  }



}
