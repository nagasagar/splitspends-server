insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1001,'nonfriendlyuser1','nonfriendlyuser1@gmail.com','$2a$10$1CylGqZyuRvES/KDpoZ1HOK7.jMLHvYAENLGvd4FX20aSNbJ1zHq2', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1002,'nonfriendlyuser2','nonfriendlyuser2@gmail.com','$2a$10$1CylGqZyuRvES/KDpoZ1HOK7.jMLHvYAENLGvd4FX20aSNbJ1zHq2', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1003,'samenameuser','samename1@gmail.com','$2a$10$1CylGqZyuRvES/KDpoZ1HOK7.jMLHvYAENLGvd4FX20aSNbJ1zHq2', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1004,'samenameuser','samename2@gmail.com','$2a$10$1CylGqZyuRvES/KDpoZ1HOK7.jMLHvYAENLGvd4FX20aSNbJ1zHq2', false, 'local');

insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1012,'nsagar','nsagar@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1014,'sirimm','sirimm@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1011,'babureddy','babureddy@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1013,'kavyar','kavyar@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1015,'narasimha','narasimha@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');


insert into grps(id, name) values (2002, 'Prague');
insert into grps(id, name) values (2009, 'Iceland');

insert into group_user(group_id, user_id) values (2002,1003);
insert into group_user(group_id, user_id) values (2002,1004);


/* iceland -> nsagar, sirimm, babureddy, kavyar, narasimha*/
insert into group_user(group_id, user_id) values (2009,1015);
insert into group_user(group_id, user_id) values (2009,1012);
insert into group_user(group_id, user_id) values (2009,1014);
insert into group_user(group_id, user_id) values (2009,1013);
insert into group_user(group_id, user_id) values (2009,1011);

/* Iceland Expenses */
insert into expenses(id, amount, detail, author_id, group_id) values (9001, 22, 'beer', 1012, 2009);
insert into expenses(id, amount, detail, author_id, group_id) values (9002, 18, 'fishNchips', 1011, 2009);
insert into expenses(id, amount, detail, author_id, group_id) values (9003, 15, 'chikenWings', 1013, 2009);
insert into expenses(id, amount, detail, author_id, group_id) values (9004, 28, 'busTickets', 1015, 2009);
insert into expenses(id, amount, detail, author_id, group_id) values (9005, 32, 'soveniers', 1014, 2009);

insert into payments(id, amount, expense_id, payee_id) values (4001, 11, 9001, 1012);
insert into payments(id, amount, expense_id, payee_id) values (4002, 11, 9001, 1011);

insert into payments(id, amount, expense_id, payee_id) values (4003, 18, 9002, 1011);

insert into payments(id, amount, expense_id, payee_id) values (4004, 10, 9003, 1013);
insert into payments(id, amount, expense_id, payee_id) values (4005, 5, 9003, 1011);

insert into payments(id, amount, expense_id, payee_id) values (4006, 28, 9004, 1015);

insert into payments(id, amount, expense_id, payee_id) values (4007, 32, 9005, 1014);

insert into shares(id, amount, expense_id, spender_id) values (5001, 8, 9001, 1012);
insert into shares(id, amount, expense_id, spender_id) values (5002, 8, 9001, 1011);
insert into shares(id, amount, expense_id, spender_id) values (5003, 6, 9001, 1015);

insert into shares(id, amount, expense_id, spender_id) values (5004, 3, 9002, 1012);
insert into shares(id, amount, expense_id, spender_id) values (5005, 3, 9002, 1014);
insert into shares(id, amount, expense_id, spender_id) values (5006, 3, 9002, 1011);
insert into shares(id, amount, expense_id, spender_id) values (5007, 6, 9002, 1013);
insert into shares(id, amount, expense_id, spender_id) values (5008, 3, 9002, 1015);

insert into shares(id, amount, expense_id, spender_id) values (5009, 5, 9003, 1012);
insert into shares(id, amount, expense_id, spender_id) values (5010, 5, 9003, 1011);
insert into shares(id, amount, expense_id, spender_id) values (5011, 5, 9003, 1015);

insert into shares(id, amount, expense_id, spender_id) values (5012, 5, 9004, 1012);
insert into shares(id, amount, expense_id, spender_id) values (5013, 5, 9004, 1014);
insert into shares(id, amount, expense_id, spender_id) values (5014, 5, 9004, 1011);
insert into shares(id, amount, expense_id, spender_id) values (5015, 5, 9004, 1013);
insert into shares(id, amount, expense_id, spender_id) values (5016, 8, 9004, 1015);

insert into shares(id, amount, expense_id, spender_id) values (5017, 8, 9005, 1012);
insert into shares(id, amount, expense_id, spender_id) values (5018, 8, 9005, 1011);
insert into shares(id, amount, expense_id, spender_id) values (5019, 8, 9005, 1013);
insert into shares(id, amount, expense_id, spender_id) values (5020, 8, 9005, 1015);
