create table if not exists ListUser (
id identity,
username varchar (50) not null,
password varchar(100)not null
);

create table if not exists TodoList(
id identity,
userId bigint not null,
title varchar (50) not null,
createdAt timestamp not null
);

alter table TodoList
add foreign key (userId) references ListUser(id);

create table if not exists TodoElement(
id identity,
listId bigint not null,
content text not null,
done bit not null 
);

alter table TodoElement
add foreign key (listId) references TodoList(id);