# InariApp
Android application for the Galileo App Competition 2018

How to use git version control:

1-Actualizar rama develop para ver si el compañero ha subido algo:

	--> git fetch origin (si ha actualizado saldra el nombre de las ramas actualizadas, sino, no saldrá nada y no hará falta el siguiente comando).
	--> git rebase -pk origin/develop
	ahora ya tenemos la branch develop actualizada con lo del compañero.

2-Realizar trabajo:
	sacas branch desde develop(primero miras si develop está actualizado con los comandos de 1):
	
	--> git checkout -b feature/(nombreRama)

	trabajas sobre ella y vas haciendo comits por cada funcionalidad que añades(como por ejemplo una vista)

	--> git add .
	--> git commit -m "vista añadida"

	cuando has finalizado el trabajo correspondiente a esa rama lo metes en develop

3-Meter trabajo realizado en develop
	antes de meter en develop, siempre hay que comprobar que no se haya actualizado. Lo hacemos como en 1.

	si se ha actualizado metemos los cambios en la branch feature/(nombreRama), sinó el siguiente comando no hace falta:
  
		-->git rebase -pk develop
    
	una vez actualizada(en caso neceario):

	--> git checkout develop
	--> git merge --no-ff feature/(nombreRama) 
	al hacer el merf se abrirá vim o algún editor de terminal para que pongas el mensaje de merge. No hace falta cambiarlo (wq + ENTER).
	ahora ya tenemos metido el trabajo en nuestro develop local y lo podemos subir

4-Subir develop

	--> git push origin develop

5-Comandos básicos

	--> git add . (añade trabajo para realizar un commit)
	--> git commit -m "mensaje de commit" (realiza un commit en la branch, puedes hacer tantos como quieras)
	--> git push origin feature/nombreRama (si se quiere subir una rama al servidor porque el trabajo está a medias).
	--> git status (da información de la rama actual y los archivos cambiados)
	--> git branch -l (lista de las ramas actuales)
	--> es aconsejable no tocar los mismos archivos los dos a la vez cuando se sale del mismo punto de develop porque cuando el segundo vaya a meter su trabajo puede dar conflicto el merge.



