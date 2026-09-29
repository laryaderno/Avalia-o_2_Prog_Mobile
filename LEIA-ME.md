# Avaliação 2 – Programação Mobile (Biomas) — Esqueleto do time

Package: `com.example.avaliacao1` · Java · Room 2.7.2 · Navigation · ViewModel + LiveData

## Quem mexe em quê (regra anti-conflito)
| Pessoa | Arquivos SEUS | Não editar |
|---|---|---|
| **P1 (Lary)** | `AppDatabase`, entidades, DAOs, `AppRepository`, `AppViewModel`, `PasswordUtils`, `MainActivity`, `nav_graph.xml`, `build.gradle`, Manifest | — |
| **P2** | `LoginFragment`, `CadastroActivity`, `fragment_login.xml`, `activity_cadastro.xml`, `strings_p2.xml`, método `atualizarToolbar()` da MainActivity | resto da MainActivity |
| **P3** | `FirstFragment`, `SecondFragment`, `ThirdFragment`, adapters (crie `ItemListAdapter`/`ItemGridAdapter`), `DetalheActivity`, `SeedDados`, layouts `fragment_first/second/third`, `item_*` | Entidades/DAOs (peça à P1) |

Se precisar de método novo no ViewModel/DAO, **peça à P1** (ou abra PR só nesse arquivo).

## Estado do esqueleto
- Banco, sessão (1:1 Sessão–Usuário), 1:N Bioma–Item, SHA-256+salt, ViewModel/LiveData: **prontos**.
- MainActivity: observer da sessão, bottom nav some no login, menu **Editar perfil / Sair** (só logado), `esconderBottomNavigation()` / `mostrarBottomNavigation()`.
- **Stubs**: `LoginFragment` e `CadastroActivity` já fazem login/cadastro/edição em texto (sem foto). `First/Second/ThirdFragment` só inflam o layout (TODO da P3). `SeedDados.popular()` é chamado uma vez na criação do banco.
- Código da Avaliação 1 (Spinner, adapters antigos, `Bioma` antigo) está em `docs/legado_avaliacao1/*.txt` para consulta.
- Depois de mudar o schema (ex.: nova coluna): **desinstale o app** do emulador (o banco é recriado).

## Convenção de dados (P1 ↔ P3)
- `Bioma.caminhoAudio` = nome do arquivo em `res/raw`, ex.: `"som_amazonia"`
- `Item.caminhoImagem` = nome do drawable, ex.: `"amazonia_img1"`
- Resolver: `getResources().getIdentifier(nome, "raw" /*ou "drawable"*/, getPackageName())`

## API do AppViewModel
`new ViewModelProvider(requireActivity()).get(AppViewModel.class)` (em Activity: `this`)
| Método | Quem usa |
|---|---|
| `login(email, senha, cb)` | P2 – LoginFragment |
| `cadastrar(nome, email, senha, foto, cb)` | P2 – CadastroActivity (cadastro) |
| `atualizarPerfil(id, nome, email, novaSenha ou null, novaFoto ou null, cb)` | P2 – CadastroActivity (edição) |
| `getUsuarioAtivo()` (`LiveData<Usuario>`, null = deslogado) | P2 – Toolbar |
| `logout()` / `((MainActivity) requireActivity()).sair()` | P2 |
| `getBiomas()` | P3 – Spinner |
| `selecionarBioma(id)` / `AppViewModel.TODOS` | P3 – Spinner |
| `getItensFiltrados()` | P3 – ListView e GridView |
| `getItem(id)` / `getBioma(id)` | P3 – DetalheActivity |
| `getRepository().executarEmBackground(...)` + `biomaDao()` / `itemDao()` | P3 – seed |

`cb` é `Resultado.Callback<Usuario>`: `r.sucesso`, `r.mensagem`, `r.dado` (sempre na main thread; cheque `isAdded()` em Fragment).

## Contratos com a P2
- Abrir a `CadastroActivity`: `putExtra(CadastroActivity.EXTRA_MODO, MODO_CADASTRO | MODO_EDICAO)`.
- **Sucesso no login não navega manualmente**: a MainActivity observa a sessão e troca de tela.
- FileProvider: authority `${applicationId}.fileprovider`, `res/xml/file_paths.xml`.
- **Foto:** reduzir antes de virar `byte[]` (lado máx. 512 px, JPEG 80%). O SQLite lê no máx. ~2 MB por linha; foto crua da câmera quebra o app.

## Git (sugestão)
```
git init && git add . && git commit -m "Esqueleto P1"
git branch -M main && git remote add origin <URL> && git push -u origin main
# cada um: git checkout -b p2-login   |   git checkout -b p3-conteudo
```

## Roteiro do vídeo (~3–4 min, máx. 10 min)
1. (0:00) Abertura: grupo, tema (biomas), o que mudou da Avaliação 1 (Room + login).
2. (0:20) Cadastro com foto da câmera; validações (campo vazio, e-mail repetido).
3. (1:00) Login: senha errada e certa; toolbar com avatar + nome e navegação inferior.
4. (1:30) Spinner com biomas do banco → muda ListView e GridView.
5. (2:15) Detalhe: tocar áudio; rolar a lista e mostrar a barra inferior esconder/aparecer.
6. (2:45) Editar Perfil: trocar nome/foto, senha em branco; toolbar atualiza.
7. (3:10) Tema escuro + rotação mantendo o estado.
8. (3:30) Sair → login, navegação some. Reabrir o app logado (sessão persistida).
9. (3:45) Código: 4 entidades, DAOs, Repository/ViewModel, SHA-256 + salt.

**Entrega (até 30/09 23:59):** vídeo no YouTube como *Não listado* + link do projeto no Google Drive ("qualquer pessoa com o link"), enviados no AVA. Alternativa: avisar por e-mail matheus.albuquerque@ufms.br até 30/09 23:59 para apresentar presencialmente em 01/10.
