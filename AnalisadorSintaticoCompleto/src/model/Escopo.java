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

    this.pai = null;
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
    Escopo escopo;

    if(this.classes.contains(tipo_var) || this.tipos_primitivos.contains(tipo_var)){
    return true;
    }else if(this.pai != null){
        escopo = this.pai;
        while (escopo.pai != null){
               if(this.classes.contains(tipo_var) || this.tipos_primitivos.contains(tipo_var)){
                     return true;
               }
               escopo = escopo.pai;
         }

     }
    return false;
  }



}
