/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author Anderson Queiroz
 */
public class AnalisadorSintatico {

    public BufferedReader codigoFonte;
    public String arquivo;
    public FileWriter arq;
    public PrintWriter salvarArq;

    public Stack pilha = new Stack();
    public String lexema = "";
    public String token = "";
    public String linhaAtual;

    public String[] s;
    public String maquina;
    int estado = 0;
    public boolean validacao = false;
    public boolean metodo_main = false;

    public final List tipo = new ArrayList();
    public final List boleano = new ArrayList();
    public final List operAritmeticos = new ArrayList();
    public final List operRelacionais = new ArrayList();
    public final List operLogicos = new ArrayList();
    public String estado_global = "";
    public String estado_semantico = "";
    public Var variavel_atual;
    public Escopo escopo_atual;
    public String var_nome = "";
    public String var_tipo = "";
    public Metodo metodo_atual;
    public ArrayList lista_tipos;
    public ArrayList lista_parametros;

    public AnalisadorSintatico(String arquivo) {

        tipo.add("int");
        tipo.add("bool");
        tipo.add("string");
        tipo.add("float");
        this.escopo_atual = new Escopo();

        boleano.add("true");
        boleano.add("false");

        try {
            codigoFonte = new BufferedReader(new FileReader(arquivo));
        } catch (FileNotFoundException ex) {
            System.err.println("Arquivo não encontrado");
        }
    }

    public void semantico(){
      //metodo chamado toda vez em que o consumir é executado

      switch (estado_global){

                case "constates":
                    if (estado_semantico.equals("tipo")){
                        variavel_atual.tipo_da_variavel = lexema;

                    } else if (estado_semantico.equals("nome")){
                      variavel_atual.nome_variavel = lexema;
                      escopo_atual.variaveis.add(variavel_atual);

                    } else if(estado_semantico.equals("init")){
                      variavel_atual = new Var("","");
                    }

                    break;

                case "dec_variavel":
                    switch(estado_semantico){

                        case "tipo":
                            variavel_atual.tipo_da_variavel = lexema;
                            break;

                        case "nome":
                            variavel_atual.nome_variavel = lexema;
                            escopo_atual.variaveis.add(variavel_atual);
                            break;

                        case "init":
                            variavel_atual = new Var("","");
                            break;

                        case "dec_classe":
                            escopo_atual.variaveis.add(new Var(var_tipo,var_nome));
                            break;

                        default :
                            break;
                    }

                    break;

                case "dec_metodo":
                    switch (estado_semantico) {
                      case "init":
                      metodo_atual = new Metodo(var_tipo,var_nome);
                      estado_semantico = "espera";
                      break;

                      case "espera":
                      break;

                      case "parametro":
                      //adicionaiona parametros do metodo
                      metodo_atual.add_parametro(new Var(var_tipo,var_nome));
                      estado_semantico = "espera";
                      break;

                      case "fim":
                      //adiciona metodo na tabela
                      metodo_atual.add_parametro(new Var(var_tipo,var_nome));
                      escopo_atual.metodos.add(metodo_atual);
                      break;

                      default:
                      break;

                    }
                    break;

                default:
                break;
              }

      }

    public String buscar_tipo(String nome ){

      // Faz uma busca em profundidade para encontrar o tipo de uma variavel atraves o nome
      ArrayList lista_variaveis;
      Escopo escopo = escopo_atual;

      while (escopo.pai != null){


          lista_variaveis = escopo.variaveis;
          for(int i = 0; i< lista_variaveis.size() ; i++){
              if(nome.equals(lista_variaveis[i].nome)){
                  return lista_variaveis[i].tipo;
              }
          }
          escopo = escopo.pai;
      }

      lista_variaveis = escopo.variaveis;
      for(int i = 0; i< lista_variaveis.size() ; i++){
          if(nome.equals(lista_variaveis[i].nome)){
              return lista_variaveis[i].tipo;
          }
      }
      return "ERRO";

    }

    public void verificar_lista_tipos(){

      String tipo_aux = lista_tipos.get(0);
      for (int i = 0 ; i <= lista_tipos.size() - 1 ; i++ ) {
          if (!(lista_tipos.get(i).equals(tipo_aux))) {
                salvarArq2.printf("Linha: %s - Erro de compatibilidade de tipos.", linhaAtual);
          }
      }

      lista_tipos.clear();
    }

    public void verificar_lista_parametros(){

      String tipo_aux = lista_parametros.get(0);

      for (int i = 0 ; i <= lista_parametros.size() - 1 ; i++ ) {
          if (!(lista_parametros.get(i).equals(tipo_aux))) {
                salvarArq2.printf("Linha: %s - Erro de compatibilidade de parametros.", linhaAtual);
          }
      }

      lista_parametros.clear();
    }

    public boolean consumir() throws IOException {

        semantico();
        arquivo = codigoFonte.readLine();
        if (arquivo == null) {
            return true;
        } else {
            s = arquivo.split(" ");
            linhaAtual = s[0];
            lexema = s[1];
            token = s[2];

            if (lexema.equals("{")) {
              escopo_atual = new Escopo(escopo_atual);
            } else if (lexema.equals("}")) {
              escopo_atual =  escopo_atual.pai;
            }
            return false;
        }


    }

    public void analisadorSintatico() throws IOException {
        arq = new FileWriter("Analisador.txt");
        salvarArq = new PrintWriter(arq);

        arq2 = new FileWriter("Analisador_Semantico.txt");
        salvarArq2 = new PrintWriter(arq);


        while (true) {
            if (consumir()) {
                break;
            }

            switch (estado) {
                case 0:
                    if (lexema.equals("final")) {
                        estado_semantico = "init";
                        constantes();
                        estado = 0;
                        break;
                    } else if (tipo.contains(lexema)) {
                        estado = 1;
                        break;
                    } else if (lexema.equals("class")) {
                        classe(0);
                        if (boleano.contains(lexema)) {
                            estado = 1;
                            var_tipo = lexema;
                        }
                        break;
                    }
                    break;

                case 1:
                    if (lexema.equals("main")) {
                        classeMain();
                        estado = 0;
                        break;
                    } else if (token.equals("Identificador")) {
                        variavel(1);
                        estado = 0;
                        break;
                    }
                    break;
            }
        }
        salvarArq.printf("Analise Sintática Concluída!");
        arq.close();

    }

    public boolean constantes() throws IOException {
        estado_global = "constates";
        if (consumir()) {
            return false;
        } else {
            if (tipo.contains(lexema)) {
              estado_semantico = "tipo";
                if (consumir()) {
                    salvarArq.printf("Linha: %s - Tipo não decladaro.%n", linhaAtual);
                    return false;
                } else {
                    if (token.equals("Identificador")) {
                      estado_semantico = "nome";
                        if (consumir()) {
                            salvarArq.printf("Linha: %s - Esperado um identificador.%n", linhaAtual);
                            return false;
                        } else {
                            if (lexema.equals(boleano) || token.equals("Número") || token.equals("Cadeia_de_Caracter")) {
                                return true;
                            }
                        }
                    }
                }

            }

        }
        salvarArq.printf("Linha: %s - Erro na decladaçao.%n", linhaAtual);
        return false;
    }

    public boolean variavel(int estadoAtual) throws IOException {
        estado_global = "dec_variavel";
        int state = estadoAtual;
        while (true) {

            if (consumir()) {
                return false;
            }

            switch (state) {
                case 0:
                    if (token.equals("Identificador")) {
                        state = 1;
                        estado_semantico = "nome";
                        break;
                    }
                    state = 15;
                    break;

                case 1:
                    if (lexema.equals(";")) {
                        return true;
                    } else if (lexema.equals(",")) {
                        state = 2;
                        break;
                    } else if (lexema.equals("[")) {
                        state = 5;
                        break;
                    }
                    state = 15;
                    break;

                case 2:
                    if (token.equals("Identificador")) {
                        state = 3;
                        var_nome = lexema;
                        estado_semantico = "dec_classe";

                        break;
                    }
                    state = 15;
                    break;
                case 3:
                    if (lexema.equals(";")) {
                        return true;
                    } else if (lexema.equals(",")) {
                        state = 2;
                        break;
                    }
                    state = 15;
                    break;

                case 5:
                    if (token.equals("Número")) {
                        state = 6;
                        break;
                    }
                    state = 15;
                    break;
                case 6:
                    if (lexema.equals("]")) {
                        state = 7;
                        break;
                    }
                    state = 15;
                    break;

                case 7:
                    if (lexema.equals("[")) {
                        state = 5;
                        break;
                    } else if (lexema.equals(";")) {
                        return true;
                    }
                    state = 15;
                    break;

                case 15:
                    salvarArq.printf("Linha: %s - Erro na declaração da variavel.%n", linhaAtual);
                    return false;
            }
        }
    }

    public boolean classe(int estadoAtual) throws IOException {
        estado_global = "dec_nova_classe";
        int state = estadoAtual;
        while (true) {
            if (consumir()) {
                return false;
            }
            switch (state) {
                case 0:
                    if (token.equals("Identificador")) {
                        state = 1;
                        escopo_atual.classes.add(lexema);
                        break;
                    }
                    salvarArq.printf("Linha: %s - Falta Identificador.%n", linhaAtual);
                    return false;

                case 1:
                    if (lexema.equals(":")) {
                        state = 4;
                        break;
                    }

                    if (lexema.equals("{")) {
                        state = 2;
                        break;
                    }

                    salvarArq.printf("Linha: %s - Erro na declaração.%n", linhaAtual);
                    return false;

                case 2:
                    if (conteudo_class()) {
                        state = 3;
                        break;
                    }
                    return false;

                case 3:
                    if (lexema.equals("}")) {
                        return true;
                    }
                    salvarArq.printf("Linha: %s - Esperava fechar a chave.%n", linhaAtual);
                    return false;

                case 4:
                    if (lexema.equals(":")) {
                        state = 5;
                        break;
                    }
                    salvarArq.printf("Linha: %s - Esperado :.", linhaAtual);
                    return false;
                case 5:
                    if (token.equals("Identificador")) {
                        state = 6;
                        break;
                    }
                    salvarArq.printf("Linha: %s - Esperado Identificador.%n", linhaAtual);
                    return false;
                case 6:
                    if (lexema.equals("{")) {
                        state = 2;
                        break;
                    }
                    salvarArq.printf("Linha: %s - Esperado {.%n", linhaAtual);
                    return false;

            }
        }

    }

    public boolean conteudo_class() throws IOException {
        estado_global = "conteudo_classe";
        int state = 0;

        while (true) {
            switch (state) {
                case 0:
                    if (tipo.contains(lexema)) {
                        state = 1;
                        var_tipo = lexema;
                        break;
                    }
                    state = 15;
                    break;

                case 1:
                    if (token.equals("Identificador")) {
                        state = 2;
                        var_nome = lexema;
                        break;
                    }
                    state = 15;
                    break;

                case 2:
                    if (lexema.equals(";")) {
                        if (variavel(4)) {
                          //declaração de variavel dentro da classe
                          estado_semantico = "dec_variavel";
                          estado_semantico = "dec_classe";
                            state = 0;
                            break;
                        }
                    } else if (lexema.equals(",")) {
                        if (variavel(2)) {
                          //declaração de variavel dentro da classe
                          estado_semantico = "dec_variavel";
                          estado_semantico = "dec_classe";
                            state = 0;
                            break;
                        }
                    } else if (lexema.equals("[")) {
                        if (variavel(5)) {
                            state = 0;
                            break;
                        }
                    }

                    if (lexema.equals("(")) {
                      //declaração de metodo
                      estado_global = "dec_metodo";
                      estado_semantico = "init";
                        if (parametro()) {
                            state = 3;
                            break;
                        }
                    }
                    state = 15;
                    break;

                case 3:
                    if (lexema.equals(")")) {
                        state = 4;
                        break;
                    }
                    state = 15;
                    break;

                case 4:
                    if (lexema.equals("{")) {
                        // colocando parametros do metodo como variaveis para o novo escopo
                        Escopo antigo = escopo_atual.pai;
                        int tamanho = antigo.metodos.size();
                        Metodo ultimo_metodo = antigo.metodos.get(tamanho-1);

                        while (!(ultimo_metodo.parametro.isEmpty())){
                          if (ultimo_metodo.parametro.size()-1 > 0) {
                              escopo_atual.variaveis.add(ultimo_metodo.parametro.remove(ultimo_metodo.parametro.size()-1));
                          }
                        }

                        if (conteudo_metodo()) {
                          if (lexema.equals("}")) {
                            state = 10;
                          }else{
                            return false;
                          }
                            state = 5;
                            break;
                        }
                        return false;
                    }
                    state = 15;
                    break;

                case 5:
                    if (lexema.equals("}")) {
                        return true;
                    }
                    state = 15;
                    break;

              case 10:
              if (tipo.contains(lexema)) {
                  state = 1;
                  var_tipo = lexema;
                  break;
              } else if (lexema.equals("}")) {
                return true;
              } else {
                state = 15;
              }
              break;

                case 15:
                    salvarArq.printf("Linha: %s - Erro no conteúdo da calsse.%n", linhaAtual);
                    return false;
            }
            if (consumir()) {
                return false;
            }
        }

    }

    public boolean conteudo_metodo() throws IOException {
        estado_global = "conteudo_metodo";
        int state = 0;

        while (true) {
            if (consumir()) {
                return false;
            }

            switch (state) {
                case 0:
                    if (tipo.contains(lexema)) {
                        estado_semantico = "tipo";
                        variavel(0);
                        state = 0;
                        break;
                    } else if (lexema.equals("print")) {
                        print();
                        state = 0;
                        break;
                    } else if (lexema.equals("for")) {
                        maquinaFor();
                        state = 0;
                        break;
                    } else if (lexema.equals("if")) {
                        maquinaIf();
                        state = 0;
                        break;
                    } else if (lexema.equals("scan")) {
                        scan();
                        state = 0;
                        break;
                    } else if (token.equals("Identificador")) {
                        state = 1;
                        lista_tipos.add(buscar_tipo(lexema));
                        break;
                    }
                    return true;

                case 1:
                    if (lexema.equals("=")) {
                        atribuicao(5);
                        state = 0;
                        break;
                    } else if (lexema.equals(":")) {
                        chamadaMetodo(3);
                        state = 0;
                        break;
                    }

                    return false;
            }
        }
    }

    public boolean parametro() throws IOException {
        estado_global = "dec_metodo";
        int state = 0;

        while (true) {
            if (consumir()) {
                return false;
            }

            switch (state) {
                case 0:
                    if (tipo.contains(lexema)) {
                        state = 1;
                        var_tipo = lexema;
                        break;
                    }
                    return false;

                case 1:
                    if (token.equals("Identificador")) {
                        state = 2;
                        var_nome = lexema;
                        break;
                    }
                    return false;

                case 2:
                    if (lexema.equals("[")) {
                        state = 3;
                        break;
                    }

                    if (lexema.equals(",")) {
                        state = 0;
                        estado_semantico = "parametro";
                        break;
                    }
                    estado_semantico = "fim";
                    return true;

                case 3:
                    if (token.equals("Número")) {
                        state = 4;
                        break;
                    }
                    return false;

                case 4:
                    if (lexema.equals("]")) {
                        state = 5;
                        break;
                    }
                    return false;

                case 5:
                    if (lexema.equals(",")) {
                        state = 0;
                        estado_semantico = "parametro";
                        break;
                    }
                    estado_semantico = "fim";
                    return true;
            }
        }
    }

    public boolean classeMain() throws IOException {
        estado_global = "classe_main";
        int state = 1;

        while (true) {
            if (consumir()) {
                return false;
            }

            switch (state) {

                case 1:
                    if (lexema.equals("(")) {
                        state = 2;
                        break;
                    }
                    state = 15;
                    break;

                case 2:
                    if (lexema.equals(")")) {
                        state = 3;
                        break;
                    }
                    state = 15;
                    break;

                case 3:
                    if (lexema.equals("{")) {
                        conteudo_metodo();
                        state = 4;
                        break;
                    }
                    state = 15;
                    break;

                case 4:
                    if (lexema.equals("}")) {
                        return true;
                    }
                    state = 15;
                    break;

                case 15:
                    salvarArq.printf("Linha: %s - Erro na escrita do main.", linhaAtual);
                    return false;
            }
        }
    }

    public boolean print() throws IOException {
        estado_global = "print";
        int state = 0;
        Stack pilhaPrint = new Stack();

        while (true) {
            if (consumir()) {
                return false;
            }

            switch (state) {
                case 0:
                    if (lexema.equals("(")) {
                        state = 1;
                        break;
                    }
                    return false;

                case 1:
                    if (lexema.equals("(")) {
                        state = 1;
                        pilhaPrint.push("(");
                        break;
                    } else if (token.equals("Cadeia_de_Caracter")) {
                        state = 2;
                        break;
                    } else if (token.equals("Identificador") || token.equals("Número")) {
                        state = 3;
                        break;
                    }
                    return false;

                case 2:
                    if (lexema.equals(")")) {
                        state = 6;
                        break;
                    }
                    return false;

                case 3:
                    if (token.equals("Operador_Aritmético")) {
                        state = 4;
                        break;
                    }
                    return false;

                case 4:
                    if (expAritmetica(4)) {
                        state = 5;
                        break;
                    }
                    return false;

                case 5:
                    if (lexema.equals(")")) {
                        state = 6;
                        break;
                    }
                    return false;

                case 6:
                    if (pilhaPrint.isEmpty() && lexema.equals(")")) {
                        state = 7;
                        break;
                    } else if (lexema.equals(")")) {
                        pilhaPrint.pop();
                        state = 6;
                        break;
                    }
                    return false;

                case 7:
                    return true;
            }

        }
    }

    public boolean scan() throws IOException {
        estado_global = "scan";
        int state = 0;

        while (true) {
            if (consumir()) {
                return false;
            }

            switch (state) {
                case 0:
                    if (lexema.equals("(")) {
                        state = 1;
                        break;
                    }
                    state = 15;
                    break;

                case 1:
                    if (token.equals("Identificador")) {
                        state = 2;
                        break;
                    }
                    state = 15;
                    break;

                case 2:
                    if (lexema.equals(",")) {
                        state = 1;
                        break;
                    } else if (lexema.equals(")")) {
                        state = 3;
                        break;
                    }
                    state = 15;
                    break;

                case 3:
                    if (lexema.equals(";")) {
                        return true;
                    }
                    state = 15;
                    break;

                case 15:
                    salvarArq.printf("Linha: %d - Erro no Scan", linhaAtual);
                    return false;
            }
        }
    }

    public boolean atribuicao(int n) throws IOException {
        estado_global = "atribuicao";
        int state = n;
        String linha = linhaAtual;

        while (true) {
            if (consumir()) {
                return false;
            }

            switch (state) {
                case 0:
                    if (token.equals("Identificador")) {
                        state = 1;
                        lista_tipos.add(buscar_tipo(lexema));
                        break;
                    }
                    state = 15;
                    break;

                case 1:
                    if (lexema.equals("[")) {
                        state = 2;
                        break;
                    } else if (lexema.equals("=")) {
                        state = 5;
                        break;
                    }
                    state = 15;
                    break;

                case 2:
                    if (token.equals("Número")) {
                        if (lexema.contains(".")) {
                          lista_tipos.add("float");
                        }else{
                          lista_tipos.add("int");
                        }
                        state = 3;
                        break;
                    }
                    state = 15;
                    break;

                case 3:
                    if (lexema.equals("]")) {
                        state = 4;
                        break;
                    }
                    state = 15;
                    break;

                case 4:
                    if (lexema.equals("=")) {
                        state = 5;
                        break;
                    } else if (lexema.equals("[")) {
                        state = 2;
                        break;
                    }
                    state = 15;
                    break;

                case 5:
                    if (token.equals("Número")) {
                        if (lexema.contains(".")) {
                          lista_tipos.add("float");
                        }else{
                          lista_tipos.add("int");
                        }
                        expLogica();
                        if (lexema.equals(";")) {
                            return true;
                        }
                        state = 15;
                        break;
                    } else if (token.equals("Identificador")) {
                        state = 6;
                        lista_tipos.add(buscar_tipo(lexema));

                        break;
                    }

                case 6:
                    if (token.equals("Operador_Relacional") || token.equals("Operador_Aritmética")
                            || token.equals("Operador_Lógica")) {
                        state = 8;
                        break;
                    } else if (lexema.equals(":")) {
                        state = 7;
                        break;
                    }
                    state = 15;
                    break;

                case 7:
                    chamadaMetodo(3);
                    if (lexema.equals(";")) {
                        verificar_lista_tipos();
                        return true;
                    }
                    state = 15;
                    break;

                case 15:
                    salvarArq.printf("Linha: %s - Erro na atribuição.", linha);
                    return false;
            }

        }

    }

    public boolean maquinaIf() throws IOException {
        estado_global = "if";
        int state = 0;

        while (true) {
            if (consumir()) {
                return false;
            }

            switch (state) {
                case 0:
                    if (lexema.equals("(")) {
                        state = 1;
                        break;
                    }
                    state = 15;
                    break;

                case 1:
                    if (expLogica()) {
                        state = 2;
                        break;
                    }
                    state = 15;
                    break;

                case 2:
                    if (lexema.equals(")")) {
                        state = 3;
                        break;
                    }
                    state = 15;
                    break;

                case 3:
                    if (lexema.equals("{")) {
                        state = 4;
                        break;
                    }
                    state = 15;
                    break;

                case 4:
                    if (conteudo_metodo()) {
                        state = 5;
                        break;
                    }
                    state = 15;
                    break;

                case 5:
                    if (lexema.equals("}")) {
                        state = 6;
                        break;
                    }
                    state = 15;
                    break;

                case 6:
                    if (lexema.equals("else")) {
                        state = 7;
                        break;
                    }
                    state = 15;
                    break;

                case 7:
                    if (lexema.equals("{")) {
                        state = 8;
                        break;
                    }
                    state = 15;
                    break;

                case 8:
                    if (conteudo_metodo()) {
                        state = 9;
                        break;
                    }
                    state = 15;
                    break;

                case 9:
                    if (lexema.equals("}")) {
                        return true;
                    }
                    state = 15;
                    break;

                case 15:
                    salvarArq.printf("Linha: %d - Erro na expressão aritmética.", linhaAtual);
                    return false;

            }

        }

    }

    public boolean maquinaFor() throws IOException {
        estado_global = "for";
        int state = 0;
        String linha = linhaAtual;

        while (true) {
            if (consumir()) {
                return false;
            }

            switch (state) {
                case 0:
                    if (lexema.equals("(")) {
                        state = 1;
                        break;
                    }
                    state = 15;
                    break;

                case 1:
                    if (atribuicao(0)) {
                        state = 2;
                        break;
                    }
                    state = 15;
                    break;

                case 2:
                    if (lexema.equals(";")) {
                        state = 3;
                        break;
                    }
                    state = 15;
                    break;

                case 3:
                    if (expLogica()) {
                        state = 4;
                        break;
                    }
                    state = 15;
                    break;

                case 4:
                    if (lexema.equals(";")) {
                        state = 5;
                        break;
                    }
                    state = 15;
                    break;

                case 5:
                    if (expAritmetica(0)) {
                        state = 6;
                        break;
                    }
                    state = 15;
                    break;

                case 6:
                    if (lexema.equals(")")) {
                        state = 7;
                        break;
                    }
                    state = 15;
                    break;

                case 7:
                    if (lexema.equals("{")) {
                        state = 8;
                        break;
                    }
                    state = 15;
                    break;

                case 8:
                    if (conteudo_metodo()) {
                        state = 9;
                        break;
                    }
                    state = 15;
                    break;

                case 9:
                    if (lexema.equals("}")) {
                        return true;
                    }
                    state = 15;
                    break;

                case 15:
                    salvarArq.printf("Linha: %s - Erro na expressão do For", linha);
                    return false;
            }
        }
    }

    public boolean expAritmetica(int n) throws IOException {
        estado_global = "exp_a";
        int state = n;
        Stack pAritmetica = new Stack();

        while (true) {
            if (consumir()) {
                return false;
            }

            switch (state) {
                case 0:
                    if (lexema.equals("(")) {
                        state = 0;
                        pAritmetica.push("(");
                        break;
                    }
                    if (token.equals("Identificador")) {
                        state = 1;
                        lista_tipos.add(buscar_tipo(lexema));
                        break;
                    } else if (token.equals("Número")) {
                      if (lexema.contains(".")) {
                        lista_tipos.add("float");
                      }else{
                        lista_tipos.add("int");
                      }
                        state = 2;
                        break;
                    }

                    state = 15;
                    break;

                case 1:
                    if (lexema.equals("[")) {
                        state = 30;
                        break;
                    } else if (token.equals("Operador_Aritmético")) {
                        state = 4;
                        break;
                    }
                    if (lexema.equals(")")) {
                        pAritmetica.pop();
                        if (pAritmetica.isEmpty()) {
                            return true;
                        }
                    }
                    if (pAritmetica.isEmpty()) {
                        return true;
                    }

                case 30:
                    if (token.equals("Número")) {
                        state = 31;
                        if (lexema.contains(".")) {
                          lista_tipos.add("float");
                        }else{
                          lista_tipos.add("int");
                        }
                        break;
                    }
                    state = 15;
                    break;

                case 31:
                    if (lexema.equals("]")) {
                        state = 32;
                        break;
                    }
                    state = 15;
                    break;

                case 32:
                    if (lexema.equals("[")) {
                        state = 30;
                        break;
                    } else if (token.equals("Operador_Relacional")) {
                        state = 3;
                        break;
                    } else if (token.equals("Operador_Aritmético")) {
                        state = 4;
                        break;
                    }
                    return true;

                case 2:
                    if (token.equals("Operador_Relacional")) {
                        state = 3;
                        break;
                    } else if (token.equals("Operador_Aritmético")) {
                        state = 4;
                        break;
                    }
                    if (lexema.equals(")")) {
                        pAritmetica.pop();
                        if (pAritmetica.isEmpty()) {
                            return true;
                        }
                    }
                    if (pAritmetica.isEmpty()) {
                        return true;
                    }

                case 4:
                    if (token.equals("Identificador")) {
                        state = 1;
                        lista_tipos.add(buscar_tipo(lexema));
                        break;
                    } else if (token.equals("Número")) {
                        state = 2;
                        if (lexema.contains(".")) {
                          lista_tipos.add("float");
                        }else{
                          lista_tipos.add("int");
                        }
                        break;
                    }
                    if (lexema.equals("(")) {
                        pAritmetica.push("(");
                        state = 0;
                        break;
                    }

                case 15:
                    salvarArq.printf("Linha: %s - Erro na Expressão Aritmética.", linhaAtual);
                    return false;
            }

        }

    }

    public boolean expLogica() throws IOException {
        estado_global = "exp_l";
        int state = 0;
        Stack pLogica = new Stack();
        String linha = linhaAtual;

        while (true) {
            if (consumir()) {
                return false;
            }

            switch (state) {
                case 0:
                    if (lexema.equals("(")) {
                        pLogica.push("(");
                        state = 0;
                        break;
                    } else if (token.equals("Identificador")) {
                        state = 1;
                        lista_tipos.add(buscar_tipo(lexema));
                        break;
                    } else if (token.equals("Número")) {
                        state = 9;
                        if (lexema.contains(".")) {
                          lista_tipos.add("float");
                        }else{
                          lista_tipos.add("int");
                        }
                        break;
                    } else if (boleano.contains(lexema)) {
                        lista_tipos.add("bool");
                        state = 3;
                        break;
                    }
                    state = 15;
                    break;

                case 1:
                    if (lexema.equals("[")) {
                        state = 20;
                        break;
                    } else if (token.equals("Operador_Relacional") || token.equals("Operador_Aritmético")) {
                        state = 8;
                        break;
                    } else if (token.equals("Operador_Lógico")) {
                        state = 4;
                        break;
                    }
                    return true;

                case 4:
                    if (token.equals("Identificador")) {
                        lista_tipos.add(buscar_tipo(lexema));
                        state = 5;
                        break;
                    } else if (lexema.equals("(")) {
                        state = 0;
                        break;
                    } else if (boleano.contains(lexema)) {
                        lista_tipos.add("bool");
                        state = 10;
                        break;
                    }
                    state = 15;
                    break;

                case 5:
                    if (lexema.equals("[")) {
                        state = 30;
                        break;
                    }
                    if (token.equals("Operador_Lógico")) {
                        state = 4;
                        break;
                    } else if (token.equals("Operador_Aritmético") || token.equals("Operador_Relacional")) {
                        state = 8;
                        break;
                    } else if (lexema.equals(")")) {
                        pLogica.pop();
                        state = 100;
                        break;
                    }

                    if (pLogica.isEmpty()) {
                        return true;
                    }
                    state = 15;
                    break;

                case 9:
                    if (token.equals("Operador_Aritmético") || token.equals("Operador_Relacional")) {
                        state = 8;
                        break;
                    } else {
                        return true;
                    }

                case 8:
                    expRelacional(3);
                    if (token.equals("Operador_Lógico")) {
                        state = 4;
                        break;
                    }
                    if (lexema.equals(")")) {
                        pLogica.pop();
                        if (pLogica.isEmpty()) {
                            return true;
                        }
                        state = 8;
                        break;
                    }
                    if (pLogica.isEmpty()) {
                        return true;
                    }

                case 10:
                    if (token.equals("Operador_Lógico")) {
                        state = 4;
                        break;
                    }
                    return true;

                case 20:
                    if (token.equals("Número")) {
                        if (lexema.contains(".")) {
                          lista_tipos.add("float");
                        }else{
                          lista_tipos.add("int");
                        }
                        state = 21;
                        break;
                    }
                    state = 15;
                    break;

                case 21:
                    if (lexema.equals("]")) {
                        state = 22;
                        break;
                    }
                    state = 15;
                    break;

                case 22:
                    if (lexema.equals("[")) {
                        state = 20;
                        break;
                    } else if (token.equals("Operador_Aritmético") || token.equals("Operador_Relacional")) {
                        state = 8;
                    } else if (token.equals("Operador_Lógico")) {
                        state = 4;
                    }
                    return true;

                case 3:
                    if (token.equals("Operador_Relacional")) {
                        state = 8;
                    } else if (token.equals("Operador_Lógico")) {
                        state = 4;
                    } else if (token.equals("Operador_Aritmético")) {
                        state = 9;
                    }
                    return true;

                case 30:
                    if (token.equals("Número")) {
                        if (lexema.contains(".")) {
                          lista_tipos.add("float");
                        }else{
                          lista_tipos.add("int");
                        }
                        state = 31;
                        break;
                    }
                    state = 15;
                    break;

                case 31:
                    if (lexema.equals("]")) {
                        state = 32;
                        break;
                    }
                    state = 15;
                    break;

                case 32:
                    if (lexema.equals("[")) {
                        state = 30;
                        break;
                    }
                    if (token.equals("Operador_Lógico")) {
                        state = 4;
                        break;
                    } else if (token.equals("Operador_Aritmético") || token.equals("Operador_Relacional")) {
                        state = 8;
                        break;
                    } else if (lexema.equals(")")) {
                        pLogica.pop();
                        state = 100;
                        break;
                    }

                    if (pLogica.isEmpty()) {
                        return true;
                    }
                    state = 15;
                    break;

                case 15:
                    salvarArq.printf("Linha: %s - Expressão lógica errada", linhaAtual);
                    if (lexema.equals(";") || lexema.equals("{") || lexema.equals("}")) {
                        return false;
                    } else {
                        state = 16;
                        break;
                    }

                case 16:
                    if (lexema.equals(";") || lexema.equals("{") || lexema.equals("}")) {
                        return false;
                    }
                    state = 16;
                    break;

                case 100:
                    if (lexema.equals(")")) {
                        pLogica.pop();
                        if (pLogica.isEmpty()) {
                            return true;
                        }
                        state = 100;
                        break;
                    } else if (pLogica.isEmpty()) {
                        return true;
                    }
            }
        }

    }

    public boolean expRelacional(int n) throws IOException {
        estado_global = "exp_r";
        int state = n;
        Stack pilhaRelacional = new Stack();

        while (true) {
            if (consumir()) {
                return false;
            }

            switch (state) {
                case 0:
                    if (lexema.equals("(")) {
                        state = 0;
                        pilhaRelacional.push("(");
                        break;
                    }
                    if (token.equals("Identificador")) {
                        lista_tipos.add(buscar_tipo(lexema));
                        state = 1;
                        break;
                    } else if (token.equals("Número")) {
                        state = 2;
                        if (lexema.contains(".")) {
                          lista_tipos.add("float");
                        }else{
                          lista_tipos.add("int");
                        }
                        break;
                    }

                    state = 15;
                    break;

                case 1:
                    if (lexema.equals("[")) {
                        state = 30;
                        break;
                    } else if (token.equals("Operador_Relacional")) {
                        state = 3;
                        break;
                    } else if (token.equals("Operador_Aritmético")) {
                        state = 4;
                        break;
                    }
                    if (lexema.equals(")")) {
                        pilhaRelacional.pop();
                        if (pilhaRelacional.isEmpty()) {
                            return true;
                        }
                    }
                    if (pilhaRelacional.isEmpty()) {
                        return true;
                    }

                case 30:
                    if (token.equals("Número")) {
                      if (lexema.contains(".")) {
                        lista_tipos.add("float");
                      }else{
                        lista_tipos.add("int");
                      }
                        state = 31;
                        break;
                    }
                    state = 15;
                    break;

                case 31:
                    if (lexema.equals("]")) {
                        state = 32;
                        break;
                    }
                    state = 15;
                    break;

                case 32:
                    if (lexema.equals("[")) {
                        state = 30;
                        break;
                    } else if (token.equals("Operador_Relacional")) {
                        state = 3;
                        break;
                    } else if (token.equals("Operador_Aritmético")) {
                        state = 4;
                        break;
                    }
                    return true;

                case 2:
                    if (token.equals("Operador_Relacional")) {
                        state = 3;
                        break;
                    } else if (token.equals("Operador_Aritmético")) {
                        state = 4;
                        break;
                    }
                    if (lexema.equals(")")) {
                        pilhaRelacional.pop();
                        if (pilhaRelacional.isEmpty()) {
                            return true;
                        }
                    }
                    if (pilhaRelacional.isEmpty()) {
                        return true;
                    }

                case 3:
                    if (token.equals("(")) {
                        pilhaRelacional.push("(");
                        state = 0;
                        break;
                    } else if (token.equals("Número")) {
                      if (lexema.contains(".")) {
                        lista_tipos.add("float");
                      }else{
                        lista_tipos.add("int");
                      }
                        state = 2;
                        break;
                    }
                    state = 15;
                    break;

                case 4:
                    expAritmetica(4);
                    if (token.equals("Operador_Relacional")) {
                        state = 3;
                        break;
                    } else if (token.equals("Operador_Aritmético")) {
                        state = 4;
                        break;
                    } else if (lexema.equals("(")) {
                        pilhaRelacional.push("(");
                        state = 0;
                        break;
                    }
                    if (lexema.equals(")")) {
                        pilhaRelacional.pop();
                        if (pilhaRelacional.isEmpty()) {
                            return true;
                        }
                    }
                    if (pilhaRelacional.isEmpty()) {
                        return true;
                    }

                case 15:
                    salvarArq.printf("Linha: %s - Erro na Expressão Regular.", linhaAtual);
                    return false;
            }

        }
    }

    public boolean chamadaMetodo(int n) throws IOException {
        estado_global = "cham_metodo";
        int state = n;
        String linha = linhaAtual;

        while (true) {
            if (consumir()) {
                return false;
            }

            switch (state) {
                case 0:
                    if (token.equals("Identificador")) {
                        state = 1;
                        break;
                    }
                    state = 15;
                    break;

                case 1:
                    if (lexema.equals("[")) {
                        state = 30;
                        break;
                    } else if (lexema.equals(":")) {
                        state = 2;
                        break;
                    }
                    state = 15;
                    break;

                case 2:
                    if (lexema.equals("(")) {
                        state = 3;
                        break;
                    }
                    state = 15;
                    break;

                case 3:
                    if (token.equals("Identificador")) {
                        state = 4;
                        lista_parametros.add(buscar_tipo(lexema));
                        break;
                    }
                    state = 15;
                    break;

                case 4:
                    if (lexema.equals(",")) {
                        state = 3;
                        break;
                    } else if (lexema.equals(")")) {
                        verificar_lista_parametros();
                        return true;
                    }
                    state = 15;
                    break;

                case 15:
                    salvarArq.printf("Linha: %s - Erro na Chamada do Método.%n", linha);

                case 30:
                    if (token.equals("Número")) {
                        state = 31;
                        break;
                    }
                    state = 15;
                    break;

                case 31:
                    if (lexema.equals("]")) {
                        state = 32;
                        break;
                    }
                    state = 15;
                    break;

                case 32:
                    if (lexema.equals("[")) {
                        state = 31;
                        break;
                    }
            }
        }
    }

}
