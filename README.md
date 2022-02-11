# BeReal
Conclusion sur le rendu du test:

Tâches développées:
- Connexion au serveur
- Parcours d'arborescence et affichage du contenu des dossiers.
- Affichage d'une image fullscreen

Tâches bonus développées:
- Suppression d'un fichier / dossier

Tâches non développées:
- Création d'un dossier
- Page de login
- Upload d'un fichier depuis le téléphone


J'ai fait le choix de simplifier l'architecture pour essayer de gagner du temps:
-Je n'ai pas utilisé de UseCase entre le ViewModel et le Repository
-Je n'ai pas créé de models par couche mais j'ai simplement utilisé le model API dans l'ensemble de l'application.

J'ai également géré la connexion au serveur en dur, ainsi que l'id du root directory pour simplifier le développement et gagner du temps.

Points à améliorer:
- Développer un vrai système de connection au server,
- Ajouter le bouton de navigation lorsque je descends dans la hiérarchie,
- Gérer l'affichage de l'image en vrai full screen en enlevant la status bar,

