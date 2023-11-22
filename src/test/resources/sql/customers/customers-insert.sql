insert into users (id, username, password, role)
 values (100, 'jonas@email.com', '$2a$12$fPHqdCZBH2oRDTlXs0.tU.voMwG9by.6d3vFeMhpUGjUldjciVVmy', 'ROLE_ADMIN'),
 (101, 'ana@email.com', '$2a$12$fPHqdCZBH2oRDTlXs0.tU.voMwG9by.6d3vFeMhpUGjUldjciVVmy','ROLE_CUSTOMER'),
  (102, 'bob@email.com', '$2a$12$fPHqdCZBH2oRDTlXs0.tU.voMwG9by.6d3vFeMhpUGjUldjciVVmy', 'ROLE_CUSTOMER'),
  (103, 'toby@email.com', '$2a$12$fPHqdCZBH2oRDTlXs0.tU.voMwG9by.6d3vFeMhpUGjUldjciVVmy', 'ROLE_CUSTOMER');

insert into customers (id, name, cpf, user_id) values
    (10, 'Ana Silva', '94140627000', 101),
    (20, 'Roberto Gomes', '89097862051', 102);