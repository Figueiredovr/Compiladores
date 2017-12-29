package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 *
 * @author Victor Figueiredo
 */
public class Var {

  public String nome_variavel;
  public String tipo_da_variavel;

  public Var (  String tipo_da_variavel, String nome_variavel){

    this.tipo_da_variavel = tipo_da_variavel;
    this.nome_variavel = nome_variavel;

  }

}
