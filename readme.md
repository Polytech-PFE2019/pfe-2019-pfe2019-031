Nous utilisons la plateforme drive pour partager nos documents.
https://drive.google.com/drive/folders/1bR3Au-MMyIRpn7MShVa3mtu9u3mKT1Zc?usp=sharing


Pour les prochains développeurs :
Il est possible d'avoir des bugs dans la partie utilisation, à cause de l'API : UsageStatsManager.
Il faut arriver à avoir les informations pour les journées de minuit à 12h59, ce qui ne se fait pas facilement.
Donc si vous avez des soucis, vous pouvez aller voir dans business/usagetime/UsageTimeProvider.

Ensuite, pour la lenteur du système, proviens toujours de cette API parce que les appels ne sont pas optimisés, vous pouvez donc regarder de ce côté.

Bon courage.

Marion.