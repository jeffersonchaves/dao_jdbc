# Laboratório DAO JDBC

Este repositório foi criado como parte da disciplina de Desenvolvimento Web III. Contém um projeto para acesso a um banco de dados relacional utilizando o **driver de conexão JDBC.** Ainda, classes com características em comum foram organizadas em pacotes. Afim de estruturar a maniulacao de dados do Banco de dados, foi utlizado o **padrão DAO**.


## Data access object

Em resumo, DAO é um padrão de projetos onde um objeto:

-   provê uma interface que abstrai o acesso a dados;
-   lê e grava a partir da origem de dados (banco de dados, arquivo, memória, etc.); e
    -   encapsula o acesso aos dados, de forma que as demais classes não precisam saber sobre isso.

## Arquitetura

Numa aplicação web comum seguindo o modelo MVC, os DAOs ficam junto com o Modelo (entidades) fazendo um trabalho de suporte, integrando a fonte de dados ao modelo de objetos do sistema.

[![inserir a descrição da imagem aqui](https://i.stack.imgur.com/rEhAT.png)](https://i.stack.imgur.com/rEhAT.png)

Fonte:  [Introdução ao JDBC](http://www.slideshare.net/utluiz/introduo-ao-jdbc)

## Responsabilidades

Seguindo o princípio de responsabilidade única, um DAO não deve ser responsável por mais do que acesso aos dados.

Definir quem faz o que pode ser um problema quando pensamos na arquitetura de um sistema, mas grande parte disso é porque misturamos as coisas.

Se olhar bem o diagrama acima, fica fácil identificarmos a responsabilidade de cada elemento.

Suponha que o usuário está acessando a página inicial do sistema web. Então uma interação comum poderia ser:

1.  Um  _controller_  recebe a requisição do usuário
2.  Esse  _controller_  chama o método do  _service_  adequado para obter as informações para aquela página
3.  O  _service_  chama um ou mais métodos de  _DAOs_  para obter as informações necessárias e retorna os dados para o  _controller_
4.  O  _controller_  recebe os dados e redireciona o usuário para uma  _view_  que vai renderizar o HTML da página

Adicionalmente, temos que:

-   Controllers executam lógica relacionada à navegação do usuário no sistema, isto é, qual URL ou qual ação exibe qual página.
-   Services executam a lógica do sistema, que pode incluir gerenciar transações e processar os dados

Portanto, em geral, cada método do DAO deve fazer uma única leitura ou gravação no banco de dados e não deve controlar transações ou realizar operações adicionais, tal como realizar alterações nos dados recebidos do serviço.

Posso dizer por experiência que muitas vezes é uma tentação você colocar regras de negócio ou controle transacional no DAO, afinal é um lugar pelo qual toda informação vai eventualmente passar. Porém, cedo ou tarde isso vai entrar em conflito com alguma outra parte do sistema. Portanto, se houver uma regra comum a ser aplicada em uma entidade, crie um serviço específico para isso e faça com que todos os outros serviços apontem para ele.

## Implementação

A vantagem de usar uma classe específica para o acesso a dados é evitar espalhar SQLs em todo lugar, tornando a manutenção e evolução de um sistema um pesadelo.

Em geral, agrupa-se os acessos aos dados por similaridade, por exemplo, uma classe por tabela. Porém, não é sempre que isso faz sentido, principalmente quando o sistema não é somente feito de cadastros simples (CRUD).

Um exemplo claro são sistemas que possuem buscas avançadas em que acessam várias tabelas. Nesse caso, cada situação precisa ser analisada caso a caso. No exemplo das buscas, um DAO específico para isso seria interessante.

Usar interfaces é opcional. Veja, Java tem uma péssima reputação por usar muitas interfaces, mas isso tem seus motivos, por exemplo:

-   Permitir várias implementações para bancos de dados diferentes sem alterar o sistema
-   Permitir versões diferentes convivendo na mesma versão do sistema (isso pode ser útil em alguns casos, como quando algum campo pode ou não existir e você quer atualizar o sistema sem obrigar a criação do campo)
-   Facilitar testes unitários criando implementações Fakes dos DAOs, por exemplo, que usam listas em memória, embora frameworks como Mockito consigam gerar mocks dinamicamente sem uma interface

## Com Interface

Abaixo, é apresentada uma implementação de um DAO que encapsula a entidade  `Usuario`.

A interface fica assim:

```java
public interface UsuarioDao {
    Usuario findByNomeUsuario(String string);
    void atualizarUltimoAcesso(Integer id, Date data);
}

```


```java
public class UsuarioDaoJDBC implements UsuarioDao {

    private DataSource ds;

    public void setDataSource(DataSource ds) {
        this.ds = ds;
    }

    public Usuario findByNomeUsuario(String string) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = ds.getConnection();
            ps = con.prepareStatement("select * from usuario where nome_usuario = ?");
            ps.setString(1, string);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario usuario =  new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNomeUsuario(rs.getString("nome_usuario"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setNome(rs.getString("nome"));
                usuario.setUltimoAcesso(rs.getTimestamp("ultimo_acesso"));
                return usuario;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void atualizarUltimoAcesso(Integer id, Date data) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = ds.getConnection();
            ps = con.prepareStatement("update usuario set ultimo_acesso = ? where id = ?");
            ps.setTimestamp(1, new java.sql.Timestamp(data.getTime()));
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

```

## Devemos usar DAO?

ORMs como Hibernate  tentam fazer todo o trabalho pesado para transitar os dados entre os objetos e as tabelas. Já vi sistemas que manipulavam diretamente as entidades JPA nas regras de negócio.

Na minha opinião, o padrão DAO ainda é, em geral, a melhor forma de se implementar a interface com o banco de dados em um sistema. Digo isso porque interagir com o banco é uma responsabilidade que faz parte de quase todo sistema e, se você não colocar no DAO, vai acabar colocando em algum outro lugar. 

Um problema desse padrão é qe ele pode não ser uma solução não é escalável, o que significa que vai gerar problemas na medida em que o sistema cresce. Justamente por causa disso, muitos usam o padrão de projeto Repository, que é um tipo de DAO um pouco diferente, mesmo quando usam JPA.



# Como Importar o Projeto

Para a importar o projeto para o Eclipse, é necessário realizar os seguintes passos:

 1. Importe o projeto para seu eclipse: File>Import > Web > WarFile
	 a. Clieque em "Browse..." e selecione o arquivo [DaoLab.war](https://github.com/jeffersonchaves/dao_jdbc/blob/main/DaoLab.war "DaoLab.war") do repositório.
	 b. Clique em finish.
 2. Crie um banco de dados de acordo com o arquivo [sql](https://github.com/jeffersonchaves/dao_jdbc/blob/main/database.sql).
 3. Configure o arquivo **db.properties**, que está na raiz da aplicação, de acordo com sus configurações.
 4. Garanta que o driver JDBC (**mysql-connector-java-8.0.27**) foi baixado e está na pasta src/main/webapp/WEB-INF/lib.

