--
-- NOTE:
--
-- File paths need to be edited. Search for $$PATH$$ and
-- replace it with the path to the directory containing
-- the extracted data files.
--
--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

--ALTER TABLE ONLY public.users_tokens DROP CONSTRAINT fke6379bb1210e7a86;
--ALTER TABLE ONLY public.contacts DROP CONSTRAINT fkde2d6053700e546e;
--ALTER TABLE ONLY public.users_groups DROP CONSTRAINT fkd034efeba6d05151;
--ALTER TABLE ONLY public.users_groups DROP CONSTRAINT fkd034efeb33195d18;
--ALTER TABLE ONLY public.users_groups DROP CONSTRAINT fkd034efeb330af4c9;
--ALTER TABLE ONLY public.systems DROP CONSTRAINT fk9871d4246b24886e;
--ALTER TABLE ONLY public.systems_users DROP CONSTRAINT fk81e61a2da6d05151;
--ALTER TABLE ONLY public.systems_users DROP CONSTRAINT fk81e61a2d33195d18;
--ALTER TABLE ONLY public.systems_users DROP CONSTRAINT fk81e61a2d330af4c9;
--ALTER TABLE ONLY public.systems_users DROP CONSTRAINT fk81e61a2d210e7a86;
--ALTER TABLE ONLY public.users DROP CONSTRAINT fk6a68e08aafb0d8e;
--ALTER TABLE ONLY public.users DROP CONSTRAINT fk6a68e08a6d05151;
--ALTER TABLE ONLY public.users DROP CONSTRAINT fk6a68e08700e546e;
--ALTER TABLE ONLY public.roles DROP CONSTRAINT fk67a8ebd330af4c9;
--ALTER TABLE ONLY public.permissions DROP CONSTRAINT fk4392f4845e466e0e;
--ALTER TABLE ONLY public.permissions DROP CONSTRAINT fk4392f484330af4c9;
--ALTER TABLE ONLY public.roles_permissions DROP CONSTRAINT fk250ae028d8762c9;
--ALTER TABLE ONLY public.roles_permissions DROP CONSTRAINT fk250ae027be3b6a6;
--ALTER TABLE ONLY public.systems_servers DROP CONSTRAINT fk1fd0d075330af4c9;
--DROP INDEX public.idx_roles_system_id;
--DROP INDEX public.idx_permissions_system_id;
--DROP INDEX public.idx_permissions_name;
--DROP INDEX public.idx_contacts_company_id;
--ALTER TABLE ONLY public.users_tokens DROP CONSTRAINT users_tokens_pkey;
--ALTER TABLE ONLY public.users DROP CONSTRAINT users_pkey;
--ALTER TABLE ONLY public.users DROP CONSTRAINT users_login_key;
--ALTER TABLE ONLY public.users_groups DROP CONSTRAINT users_groups_pkey;
--ALTER TABLE ONLY public.users_groups DROP CONSTRAINT users_groups_name_system_id_key;
--ALTER TABLE ONLY public.systems_users DROP CONSTRAINT systems_users_pkey;
--ALTER TABLE ONLY public.systems_servers DROP CONSTRAINT systems_servers_pkey;
--ALTER TABLE ONLY public.systems_servers DROP CONSTRAINT systems_servers_name_system_id_key;
--ALTER TABLE ONLY public.systems DROP CONSTRAINT systems_pkey;
--ALTER TABLE ONLY public.systems DROP CONSTRAINT systems_name_key;
--ALTER TABLE ONLY public.roles DROP CONSTRAINT roles_pkey;
--ALTER TABLE ONLY public.roles_permissions DROP CONSTRAINT roles_permissions_pkey;
--ALTER TABLE ONLY public.roles DROP CONSTRAINT roles_bit_index_system_id_key;
--ALTER TABLE ONLY public.permissions DROP CONSTRAINT permissions_pkey;
--ALTER TABLE ONLY public.permissions DROP CONSTRAINT permissions_is_node_name_parent_id_key;
--ALTER TABLE ONLY public.contacts DROP CONSTRAINT contacts_pkey;
--ALTER TABLE ONLY public.contacts DROP CONSTRAINT contacts_email_key;
--ALTER TABLE ONLY public.companies DROP CONSTRAINT companies_pkey;
--ALTER TABLE ONLY public.companies DROP CONSTRAINT companies_name_key;
--DROP TABLE public.users_tokens;
--DROP SEQUENCE public.users_id_seq;
--DROP TABLE public.users_groups;
--DROP TABLE public.users;
--DROP TABLE public.systems_users;
--DROP TABLE public.systems_servers;
--DROP TABLE public.systems;
--DROP TABLE public.roles_permissions;
--DROP TABLE public.roles;
--DROP TABLE public.permissions;
--DROP SEQUENCE public.hibernate_sequence;
--DROP TABLE public.contacts;
--DROP TABLE public.companies;
--DROP EXTENSION plpgsql;
--DROP SCHEMA public;
--
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

--CREATE SCHEMA public;


--ALTER SCHEMA public OWNER TO postgres;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

--COMMENT ON SCHEMA public IS 'standard public schema';


--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

--CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

--COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: companies; Type: TABLE; Schema: public; Owner: admin_admin; Tablespace: 
--

CREATE TABLE companies (
    id bigint NOT NULL,
    name character varying(64) NOT NULL
);


ALTER TABLE public.companies OWNER TO admin_admin;

--
-- Name: contacts; Type: TABLE; Schema: public; Owner: admin_admin; Tablespace: 
--

CREATE TABLE contacts (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    firstname character varying(32) NOT NULL,
    middlename character varying(32),
    phone bigint,
    secondname character varying(32) NOT NULL,
    company_id bigint NOT NULL
);


ALTER TABLE public.contacts OWNER TO admin_admin;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: admin_admin
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO admin_admin;

--
-- Name: permissions; Type: TABLE; Schema: public; Owner: admin_admin; Tablespace: 
--

CREATE TABLE permissions (
    id bigint NOT NULL,
    is_node boolean NOT NULL,
    name character varying(80) NOT NULL,
    parent_id bigint,
    title character varying(256),
    system_id bigint NOT NULL
);


ALTER TABLE public.permissions OWNER TO admin_admin;

--
-- Name: roles; Type: TABLE; Schema: public; Owner: admin_admin; Tablespace: 
--

CREATE TABLE roles (
    id bigint NOT NULL,
    bit_index integer NOT NULL,
    title character varying(64) NOT NULL,
    system_id bigint NOT NULL
);


ALTER TABLE public.roles OWNER TO admin_admin;

--
-- Name: roles_permissions; Type: TABLE; Schema: public; Owner: admin_admin; Tablespace: 
--

CREATE TABLE roles_permissions (
    role_id bigint NOT NULL,
    permission_id bigint NOT NULL
);


ALTER TABLE public.roles_permissions OWNER TO admin_admin;

--
-- Name: systems; Type: TABLE; Schema: public; Owner: admin_admin; Tablespace: 
--

CREATE TABLE systems (
    id bigint NOT NULL,
    name character varying(32) NOT NULL,
    password character varying(32) NOT NULL,
    url character varying(128),
    parent_id bigint
);


ALTER TABLE public.systems OWNER TO admin_admin;

--
-- Name: systems_servers; Type: TABLE; Schema: public; Owner: admin_admin; Tablespace: 
--

CREATE TABLE systems_servers (
    id bigint NOT NULL,
    name character varying(32) NOT NULL,
    url character varying(128) NOT NULL,
    system_id bigint NOT NULL
);


ALTER TABLE public.systems_servers OWNER TO admin_admin;

--
-- Name: systems_users; Type: TABLE; Schema: public; Owner: admin_admin; Tablespace: 
--

CREATE TABLE systems_users (
    inherited_roles bigint,
    modified_date timestamp without time zone,
    roles bigint,
    user_id integer NOT NULL,
    system_id bigint NOT NULL,
    modifiedby_id integer,
    parent_id bigint
);


ALTER TABLE public.systems_users OWNER TO admin_admin;

--
-- Name: users; Type: TABLE; Schema: public; Owner: admin_admin; Tablespace: 
--

CREATE TABLE users (
    id integer NOT NULL,
    active boolean NOT NULL,
    created timestamp without time zone NOT NULL,
    last_login_date timestamp without time zone,
    ldap_domain character varying(64),
    login character varying(64) NOT NULL,
    modified_date timestamp without time zone,
    passhash character varying(40),
    company_id bigint NOT NULL,
    contact_id bigint NOT NULL,
    modifiedby_id integer
);


ALTER TABLE public.users OWNER TO admin_admin;

--
-- Name: users_groups; Type: TABLE; Schema: public; Owner: admin_admin; Tablespace: 
--

CREATE TABLE users_groups (
    id bigint NOT NULL,
    inherited_roles bigint,
    modified_date timestamp without time zone NOT NULL,
    name character varying(64) NOT NULL,
    parent_id bigint,
    roles bigint,
    modifiedby_id integer NOT NULL,
    system_id bigint NOT NULL
);


ALTER TABLE public.users_groups OWNER TO admin_admin;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: admin_admin
--

CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO admin_admin;

--
-- Name: users_tokens; Type: TABLE; Schema: public; Owner: admin_admin; Tablespace: 
--

CREATE TABLE users_tokens (
    id character varying(40) NOT NULL,
    expire_date timestamp without time zone NOT NULL,
    remember_me boolean,
    user_id integer NOT NULL
);


ALTER TABLE public.users_tokens OWNER TO admin_admin;

--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: admin_admin
--

SELECT pg_catalog.setval('hibernate_sequence', 23, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: admin_admin
--

SELECT pg_catalog.setval('users_id_seq', 2, true);


--
-- Name: companies_name_key; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY companies
    ADD CONSTRAINT companies_name_key UNIQUE (name);


--
-- Name: companies_pkey; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY companies
    ADD CONSTRAINT companies_pkey PRIMARY KEY (id);


--
-- Name: contacts_email_key; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY contacts
    ADD CONSTRAINT contacts_email_key UNIQUE (email);


--
-- Name: contacts_pkey; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY contacts
    ADD CONSTRAINT contacts_pkey PRIMARY KEY (id);


--
-- Name: permissions_is_node_name_parent_id_key; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY permissions
    ADD CONSTRAINT permissions_is_node_name_parent_id_key UNIQUE (is_node, name, parent_id);


--
-- Name: permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY permissions
    ADD CONSTRAINT permissions_pkey PRIMARY KEY (id);


--
-- Name: roles_bit_index_system_id_key; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT roles_bit_index_system_id_key UNIQUE (bit_index, system_id);


--
-- Name: roles_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY roles_permissions
    ADD CONSTRAINT roles_permissions_pkey PRIMARY KEY (role_id, permission_id);


--
-- Name: roles_pkey; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: systems_name_key; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY systems
    ADD CONSTRAINT systems_name_key UNIQUE (name);


--
-- Name: systems_pkey; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY systems
    ADD CONSTRAINT systems_pkey PRIMARY KEY (id);


--
-- Name: systems_servers_name_system_id_key; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY systems_servers
    ADD CONSTRAINT systems_servers_name_system_id_key UNIQUE (name, system_id);


--
-- Name: systems_servers_pkey; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY systems_servers
    ADD CONSTRAINT systems_servers_pkey PRIMARY KEY (id);


--
-- Name: systems_users_pkey; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY systems_users
    ADD CONSTRAINT systems_users_pkey PRIMARY KEY (system_id, user_id);


--
-- Name: users_groups_name_system_id_key; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY users_groups
    ADD CONSTRAINT users_groups_name_system_id_key UNIQUE (name, system_id);


--
-- Name: users_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY users_groups
    ADD CONSTRAINT users_groups_pkey PRIMARY KEY (id);


--
-- Name: users_login_key; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_login_key UNIQUE (login);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: admin_admin; Tablespace: 
--

ALTER TABLE ONLY users_tokens
    ADD CONSTRAINT users_tokens_pkey PRIMARY KEY (id);


--
-- Name: idx_contacts_company_id; Type: INDEX; Schema: public; Owner: admin_admin; Tablespace: 
--

CREATE INDEX idx_contacts_company_id ON contacts USING btree (company_id);


--
-- Name: idx_permissions_name; Type: INDEX; Schema: public; Owner: admin_admin; Tablespace: 
--

CREATE INDEX idx_permissions_name ON permissions USING btree (name);


--
-- Name: idx_permissions_system_id; Type: INDEX; Schema: public; Owner: admin_admin; Tablespace: 
--

CREATE INDEX idx_permissions_system_id ON permissions USING btree (system_id);


--
-- Name: idx_roles_system_id; Type: INDEX; Schema: public; Owner: admin_admin; Tablespace: 
--

CREATE INDEX idx_roles_system_id ON roles USING btree (system_id);


--
-- Name: fk1fd0d075330af4c9; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY systems_servers
    ADD CONSTRAINT fk1fd0d075330af4c9 FOREIGN KEY (system_id) REFERENCES systems(id);


--
-- Name: fk250ae027be3b6a6; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY roles_permissions
    ADD CONSTRAINT fk250ae027be3b6a6 FOREIGN KEY (role_id) REFERENCES roles(id);


--
-- Name: fk250ae028d8762c9; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY roles_permissions
    ADD CONSTRAINT fk250ae028d8762c9 FOREIGN KEY (permission_id) REFERENCES permissions(id);


--
-- Name: fk4392f484330af4c9; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY permissions
    ADD CONSTRAINT fk4392f484330af4c9 FOREIGN KEY (system_id) REFERENCES systems(id);


--
-- Name: fk4392f4845e466e0e; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY permissions
    ADD CONSTRAINT fk4392f4845e466e0e FOREIGN KEY (parent_id) REFERENCES permissions(id);


--
-- Name: fk67a8ebd330af4c9; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT fk67a8ebd330af4c9 FOREIGN KEY (system_id) REFERENCES systems(id);


--
-- Name: fk6a68e08700e546e; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY users
    ADD CONSTRAINT fk6a68e08700e546e FOREIGN KEY (company_id) REFERENCES companies(id);


--
-- Name: fk6a68e08a6d05151; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY users
    ADD CONSTRAINT fk6a68e08a6d05151 FOREIGN KEY (modifiedby_id) REFERENCES users(id);


--
-- Name: fk6a68e08aafb0d8e; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY users
    ADD CONSTRAINT fk6a68e08aafb0d8e FOREIGN KEY (contact_id) REFERENCES contacts(id);


--
-- Name: fk81e61a2d210e7a86; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY systems_users
    ADD CONSTRAINT fk81e61a2d210e7a86 FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: fk81e61a2d330af4c9; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY systems_users
    ADD CONSTRAINT fk81e61a2d330af4c9 FOREIGN KEY (system_id) REFERENCES systems(id);


--
-- Name: fk81e61a2d33195d18; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY systems_users
    ADD CONSTRAINT fk81e61a2d33195d18 FOREIGN KEY (parent_id) REFERENCES users_groups(id);


--
-- Name: fk81e61a2da6d05151; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY systems_users
    ADD CONSTRAINT fk81e61a2da6d05151 FOREIGN KEY (modifiedby_id) REFERENCES users(id);


--
-- Name: fk9871d4246b24886e; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY systems
    ADD CONSTRAINT fk9871d4246b24886e FOREIGN KEY (parent_id) REFERENCES systems(id);


--
-- Name: fkd034efeb330af4c9; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY users_groups
    ADD CONSTRAINT fkd034efeb330af4c9 FOREIGN KEY (system_id) REFERENCES systems(id);


--
-- Name: fkd034efeb33195d18; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY users_groups
    ADD CONSTRAINT fkd034efeb33195d18 FOREIGN KEY (parent_id) REFERENCES users_groups(id);


--
-- Name: fkd034efeba6d05151; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY users_groups
    ADD CONSTRAINT fkd034efeba6d05151 FOREIGN KEY (modifiedby_id) REFERENCES users(id);


--
-- Name: fkde2d6053700e546e; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY contacts
    ADD CONSTRAINT fkde2d6053700e546e FOREIGN KEY (company_id) REFERENCES companies(id);


--
-- Name: fke6379bb1210e7a86; Type: FK CONSTRAINT; Schema: public; Owner: admin_admin
--

ALTER TABLE ONLY users_tokens
    ADD CONSTRAINT fke6379bb1210e7a86 FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Ensure that admin user has proper rights on all tables
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO admin;
GRANT USAGE, SELECT, UPDATE ON ALL SEQUENCES IN SCHEMA public TO admin;

-- Insert data
INSERT INTO systems(id, name, password, url, parent_id)
  VALUES (1, 'admin', 'sdf458', 'http://localhost:9090/admin/admin.html', NULL);

--
-- PostgreSQL database dump complete
--

