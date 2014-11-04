# publicBlog

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Create database and specifications

This blog assumes a mysql database connection.  First create a database in mysql named "blog".

Create your own dbspec.txt file that contains the following:

	{:db "blog", :user "user", :password "****"}

Edit line 6 of the schema.clj file so the "~/dbspec.txt" contains the path of where you saved the dbspec.txt file.

## Creating the tables

To create the database tables for your blog, do the following:
```cmd
$ cd publicBlog
$ lein repl
publicBlog.repl=> (in-ns 'publicBlog.db.schema)
#<Namespace publicBlog.db.schema>
publicBlog.db.schema=> (create-user-table)
>(0)
publicBlog.db.schema=> (create-post-table)
>(0)
publicBlog.db.schema=> (create-tag-table)
>(0)
publicBlog.db.schema=> (create-post-tags-table)
>(0)
publicBlog.db.schema=> (create-post-tags-index)
>[0]
publicBlog.db.schema=> exit
```
## Running

To start a web server for the application, run:

    lein ring server

## Using the blog

When using the blog for the first time, go to http://localhost:3000/register to create a user name and password.  After creating a user name and password, I recommend removing the /register routes and removing the functions relating to registration in the auth.clj file.

You can login with your newly created user name and password at http://localhost:3000/login
