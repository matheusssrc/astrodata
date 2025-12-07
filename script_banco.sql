-----------------------------------------------------------------------------
-- TRABALHO FINAL POO - SCRIPT DE BANCO DE DADOS
-- ALUNOS: Matheus Rossi Carvalho e Rafaela Aguirre Clamer
-----------------------------------------------------------------------------
-- INSTRUÇÕES DE EXECUÇÃO:
-- 1. Abra o pgAdmin ou DBeaver.
-- 2. Execute a "SEÇÃO 1" para garantir que o usuário e o banco existam.
-- 3. Conecte-se ao banco 'poo_tf'.
-- 4. Execute a "SEÇÃO 2" para criar a tabela e carregar os dados.
-----------------------------------------------------------------------------

-- ==========================================================================
-- SEÇÃO 1: INFRAESTRUTURA (Executar como root/postgres)
-- ==========================================================================

-- 1. Cria o usuário 'poo_user' com a senha configurada no Java
DO
$do$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'poo_user') THEN
      CREATE ROLE poo_user LOGIN PASSWORD 'poo_senha123';
   END IF;
END
$do$;

-- 2. Cria o banco de dados (se não existir, execute a linha abaixo separadamente)
-- CREATE DATABASE poo_tf WITH OWNER = poo_user ENCODING = 'UTF8';

-- ==========================================================================
-- SEÇÃO 2: ESTRUTURA E DADOS (Executar CONECTADO no banco 'poo_tf')
-- ==========================================================================

-- 3. GARANTIA DE PERMISSÕES (Correção para PostgreSQL 15+)
-- Garante que o usuário possa acessar o esquema public e criar objetos nele
GRANT USAGE, CREATE ON SCHEMA public TO poo_user;

-- 4. Remove tabela antiga se existir para evitar conflitos
DROP TABLE IF EXISTS public.corpos_celestes;

-- 5. Cria a tabela
CREATE TABLE public.corpos_celestes (
    id SERIAL PRIMARY KEY,
    tipo text NOT NULL,
    nome text NOT NULL,
    massa_kg double precision NOT NULL,
    distancia_anos_luz bigint NOT NULL,
    atributo_especial text
);

-- Define o dono da tabela como o usuário do sistema Java
ALTER TABLE public.corpos_celestes OWNER TO poo_user;

-- Garante permissões totais na tabela recém-criada
GRANT ALL PRIVILEGES ON TABLE public.corpos_celestes TO poo_user;
GRANT ALL PRIVILEGES ON SEQUENCE public.corpos_celestes_id_seq TO poo_user;

-- 6. Inserção de Dados (Carga Inicial)
INSERT INTO public.corpos_celestes (id, tipo, nome, massa_kg, distancia_anos_luz, atributo_especial) VALUES 
(1, 'ESTRELA', 'Sol', 1.989e+30, 0, 'G2V'),
(2, 'ESTRELA', 'Sirius A', 4.018e+30, 8, 'A1V'),
(3, 'ESTRELA', 'Alpha Centauri A', 2.193e+30, 4, 'G2V'),
(4, 'ESTRELA', 'Proxima Centauri', 2.446e+29, 4, 'M6V'),
(5, 'PLANETA', 'Terra', 5.972e+24, 0, 'true'),
(6, 'PLANETA', 'Marte', 6.417e+23, 0, 'false'),
(7, 'PLANETA', 'Kepler-186f', 5.972e+24, 500, 'true'),
(8, 'PLANETA', 'Jupiter', 1.898e+27, 0, 'false'),
(9, 'PLANETA', 'Venus', 4.867e+24, 0, 'false'),
(10, 'PLANETA', 'Saturno', 5.683e+26, 0, 'false'),
(11, 'PLANETA', 'HD 40307 g', 7.12e+25, 42, 'true'),
(27, 'ESTRELA', 'Vega', 4.25646e+30, 25, 'A0V'),
(28, 'ESTRELA', 'Barnards Star', 2.86416e+29, 6, 'M4Ve'),
(29, 'ESTRELA', 'Betelgeuse', 2.3868e+31, 550, 'M2Iab'),
(30, 'ESTRELA', 'Rigel', 4.1769e+31, 860, 'B8Ia'),
(31, 'ESTRELA', 'Deneb', 3.7791e+31, 2600, 'A2Ia'),
(32, 'ESTRELA', 'Tau Ceti', 1.55142e+30, 12, 'G8V'),
(33, 'PLANETA', 'Proxima Centauri b', 7.58444e+24, 4, 'true'),
(34, 'PLANETA', 'TRAPPIST-1e', 4.610384e+24, 40, 'true'),
(35, 'PLANETA', 'TRAPPIST-1f', 4.06096e+24, 40, 'true'),
(36, 'PLANETA', 'TRAPPIST-1g', 8.00248e+24, 40, 'true'),
(37, 'PLANETA', 'Gliese 667 Cc', 2.26936e+25, 23, 'true'),
(38, 'PLANETA', 'Kepler-452b', 2.986e+25, 1400, 'true'),
(39, 'PLANETA', 'HD 209458 b', 1.30962e+27, 150, 'false'),
(40, 'PLANETA', '51 Pegasi b', 8.7308e+26, 50, 'false'),
(41, 'PLANETA', 'HD 189733 b', 2.16372e+27, 64, 'false'),
(42, 'PLANETA', 'WASP-12 b', 2.6572e+27, 1400, 'false');

-- 7. Ajuste da Sequência (MUITO IMPORTANTE)
-- Atualiza o contador do ID para 50. O próximo cadastro será o 51.
SELECT pg_catalog.setval('public.corpos_celestes_id_seq', 50, true);