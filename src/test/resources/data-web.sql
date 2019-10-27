insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1001,'nsahas','nsahas@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1002,'nsagar','nsagar@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1003,'sirimm','sirimm@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1004,'sneham','sneham@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1005,'sudaya','sudaya@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1006,'subram','subram@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1007,'shaurya','shaurya@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1008,'nsanvi','nsanvi@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1009,'dhiyap','dhiyap@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1010,'aradhya','aradhya@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1011,'babureddy','babureddy@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1012,'abhishek','abhishek@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1013,'kavyar','kavyar@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1014,'epavan','epavan@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1015,'narasimha','narasimha@gmail.com','$2a$10$bx1sZ36pFcEhRFxs8y9QfeFZpmPiFPrir2A6C1ELIuZlEYAcMY/B6', false, 'local');
/* nsagar -> nsahas, sirimm, sneham, sudaya, subram, shaurya, nsanvi, dhiyap, aradhya, babureddy, kavyar, epavan */
insert into friends(user_id,friend_id) values (1002,1001);
insert into friends(user_id,friend_id) values (1002,1003);
insert into friends(user_id,friend_id) values (1002,1004);
insert into friends(user_id,friend_id) values (1002,1005);
insert into friends(user_id,friend_id) values (1002,1006);
insert into friends(user_id,friend_id) values (1002,1007);
insert into friends(user_id,friend_id) values (1002,1008);
insert into friends(user_id,friend_id) values (1002,1009);
insert into friends(user_id,friend_id) values (1002,1010);
insert into friends(user_id,friend_id) values (1002,1011);
insert into friends(user_id,friend_id) values (1002,1013);
insert into friends(user_id,friend_id) values (1002,1014);
/* sirimm -> sneham, sudaya, subram | nsagar */
insert into friends(user_id,friend_id) values (1003,1004);
insert into friends(user_id,friend_id) values (1003,1005);
insert into friends(user_id,friend_id) values (1003,1006);
/* sneham -> aradhya, babureddy, abhishek, kavyar | nsagar */
insert into friends(user_id,friend_id) values (1004,1010);
insert into friends(user_id,friend_id) values (1004,1011);
insert into friends(user_id,friend_id) values (1004,1012);
insert into friends(user_id,friend_id) values (1004,1013);
/* shaurya -> nsahas, nsagar, sirimm, sneham*/
insert into friends(user_id,friend_id) values (1007,1001);
insert into friends(user_id,friend_id) values (1007,1002);
insert into friends(user_id,friend_id) values (1007,1003);
insert into friends(user_id,friend_id) values (1007,1004);
/* nsahas -> sirimm | nsagar, shaurya */
insert into friends(user_id,friend_id) values (1001,1003);

insert into grps(id, name) values (2001, 'October Fest');
insert into grps(id, name) values (2002, 'Prague');
insert into grps(id, name) values (2003, 'London');
insert into grps(id, name) values (2004, 'Turkey');
insert into grps(id, name) values (2005, 'Portugal');
insert into grps(id, name) values (2006, 'Spain');
insert into grps(id, name) values (2007, 'House Party');
insert into grps(id, name) values (2008, 'Scandanavia Grand');
insert into grps(id, name) values (2009, 'Iceland');
/* iceland -> nsagar, sirimm, babureddy, kavyar, narasimha*/
insert into group_user(group_id, user_id) values (2009,1015);
insert into group_user(group_id, user_id) values (2009,1002);
insert into group_user(group_id, user_id) values (2009,1003);
insert into group_user(group_id, user_id) values (2009,1013);
insert into group_user(group_id, user_id) values (2009,1011);
/* houseParty -> narasimha, nsagar, sirimm, babureddy, kavyar*/
insert into group_user(group_id, user_id) values (2007,1015);
insert into group_user(group_id, user_id) values (2007,1002);
insert into group_user(group_id, user_id) values (2007,1003);
insert into group_user(group_id, user_id) values (2007,1013);
insert into group_user(group_id, user_id) values (2007,1011);
/* October Fest -> narasimha, nsagar, babureddy, abhishek, epavan */
insert into group_user(group_id, user_id) values (2001,1015);
insert into group_user(group_id, user_id) values (2001,1002);
insert into group_user(group_id, user_id) values (2001,1011);
insert into group_user(group_id, user_id) values (2001,1012);
insert into group_user(group_id, user_id) values (2001,1014);
/* spain -> nsagar, sirimm, shaurya*/
insert into group_user(group_id, user_id) values (2006,1002);
insert into group_user(group_id, user_id) values (2006,1003);
insert into group_user(group_id, user_id) values (2006,1007);