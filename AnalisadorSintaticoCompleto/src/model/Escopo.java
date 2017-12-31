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
  public List tipos_primitivos = new ArrayList();

  public Escopo (){

    tipos_primitivos.add("int");
    tipos_primitivos.add("bool");
    tipos_primitivos.add("string");
    tipos_primitivos.add("float");

  }

  public Escopo (Escopo anterior){
    this.pai = anterior;

    tipos_primitivos.add("int");
    tipos_primitivos.add("bool");
    tipos_primitivos.add("string");
    tipos_primitivos.add("float");

  }

  public boolean add_variavel (Var nova){
    Var variavel;
    if (!verificar_tipo(nova.tipo_da_variavel)) {
      return false;
    }else {
      //verifica se a variavel a ser adicionada ja existe nesse escopo
      for (int i = 0 ; i< this.variaveis.size(); i++){
        variavel = (Var) this.variaveis.get(i);
        if (nova.nome_variavel.equals(variavel.nome_variavel)) {
          return false;
        }
      }
      this.variaveis.add(nova);
      return true;
    }
  }

  public boolean verificar_tipo(String tipo_var ){
    // Faz uma busca em profundidade para encontrar o tipo de uma variavel atraves o nome
    ArrayList lista_tipos;
    Var variavel_aux;
    while (pai != null){
      for(int i = 0; i< this.classes.size(); i++){
        if(classes.contains(tipo_var) || tipos_primitivos.contains(tipo_var) ){
          return true;
        }
      }      
    }
    return false;
  }



}
