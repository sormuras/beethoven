/open .bach/Bach.java

Bach bach = new Bach(Level.CONFIG)
bach.set(Bach.Folder.DEPENDENCIES, Paths.get("dependencies"))
bach.clean()
bach.prepare(Paths.get("modules"))
bach.compile()

/exit
