create table post (
	id serial primary key,
	jobName varchar(255),
	jobText text,
	link text UNIQUE,
	created timestamp
)