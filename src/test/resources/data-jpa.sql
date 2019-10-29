insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1001,'nonfriendlyuser1','nonfriendlyuser1@gmail.com','$2a$10$1CylGqZyuRvES/KDpoZ1HOK7.jMLHvYAENLGvd4FX20aSNbJ1zHq2', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1002,'nonfriendlyuser2','nonfriendlyuser2@gmail.com','$2a$10$1CylGqZyuRvES/KDpoZ1HOK7.jMLHvYAENLGvd4FX20aSNbJ1zHq2', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1003,'samenameuser','samename1@gmail.com','$2a$10$1CylGqZyuRvES/KDpoZ1HOK7.jMLHvYAENLGvd4FX20aSNbJ1zHq2', false, 'local');
insert into users(id, name, email, password, EMAIL_VERIFIED, PROVIDER) values (1004,'samenameuser','samename2@gmail.com','$2a$10$1CylGqZyuRvES/KDpoZ1HOK7.jMLHvYAENLGvd4FX20aSNbJ1zHq2', false, 'local');

insert into grps(id, name) values (2002, 'Prague');

insert into group_user(group_id, user_id) values (2002,1003);
insert into group_user(group_id, user_id) values (2002,1004);