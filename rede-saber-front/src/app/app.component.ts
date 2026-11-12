import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';

interface LoginResponse {
  id: string;
  nome: string;
  email: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  private readonly baseUrl = 'http://localhost:8080';
  private readonly requestOptions = { withCredentials: true };

  isLoggedIn = false;
  mostrarTelaCadastro = false;
  abaAtiva: 'visao-geral' | 'usuarios' | 'materiais' | 'avaliacoes' = 'visao-geral';
  carregandoLogin = false;
  carregandoCadastro = false;
  carregandoUsuarios = false;
  carregandoMateriais = false;
  carregandoAvaliacoes = false;
  salvando = false;

  usuarioLogado: LoginResponse | null = null;
  usuarios: any[] = [];
  materiais: any[] = [];
  avaliacoes: any[] = [];

  mensagemErro = '';
  mensagemSucesso = '';

  loginData = { email: '', senha: '' };
  novoUsuario = { nome: '', email: '', senha: '', telefone: '', rua: '', cidade: '', estado: '' };
  novoMaterial = { titulo: '', descricao: '', linkArquivo: '' };
  novaAvaliacao = { nota: 5, comentarioGeral: '', autorId: '', materialId: '', criterioNome: 'Clareza', criterioPeso: 10 };

  usuarioEmEdicao: any = null;
  materialEmEdicao: any = null;
  avaliacaoEmEdicao: any = null;
  registroDetalhe: any = null;
  tipoDetalhe = '';
  novoComentario: Record<string, string> = {};
  buscaUsuarios = '';
  buscaMateriais = '';
  buscaAvaliacoes = '';

  constructor(private readonly http: HttpClient, private readonly cdr: ChangeDetectorRef) {}

  ngOnInit(): void {}

  navegarPara(aba: 'visao-geral' | 'usuarios' | 'materiais' | 'avaliacoes'): void {
    console.log('Navegação acionada:', aba);
    this.abaAtiva = aba;
    this.cdr.detectChanges();
  }

  get usuariosFiltrados(): any[] {
    const termo = this.buscaUsuarios.trim().toLowerCase();
    if (!termo) return this.usuarios;
    return this.usuarios.filter(u => `${u.nome ?? ''} ${u.email ?? ''} ${u.telefone ?? ''}`.toLowerCase().includes(termo));
  }

  get materiaisFiltrados(): any[] {
    const termo = this.buscaMateriais.trim().toLowerCase();
    if (!termo) return this.materiais;
    return this.materiais.filter(m => `${m.titulo ?? ''} ${m.descricao ?? ''}`.toLowerCase().includes(termo));
  }

  get avaliacoesFiltradas(): any[] {
    const termo = this.buscaAvaliacoes.trim().toLowerCase();
    if (!termo) return this.avaliacoes;
    return this.avaliacoes.filter(a => `${a.comentarioGeral ?? ''} ${a.materialId ?? ''} ${a.usuarioId ?? ''}`.toLowerCase().includes(termo));
  }

  fazerLogin(): void {
    const email = this.loginData.email.trim();
    if (!email || !this.loginData.senha) {
      this.exibirErro('Informe seu e-mail e sua senha para continuar.');
      return;
    }

    this.carregandoLogin = true;
    this.limparMensagens();

    this.http.post<LoginResponse>(`${this.baseUrl}/users/login`, { email, senha: this.loginData.senha }, this.requestOptions)
      .pipe(finalize(() => { this.carregandoLogin = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: usuario => {
          if (!usuario?.id) {
            this.exibirErro('O servidor respondeu, mas não retornou os dados do usuário. Confira a resposta de /users/login.');
            return;
          }
          this.usuarioLogado = usuario;
          this.isLoggedIn = true;
          this.cdr.detectChanges();
          this.mostrarTelaCadastro = false;
          this.abaAtiva = 'visao-geral';
          this.loginData = { email: '', senha: '' };
          this.limparMensagens();
          this.carregarDados();
        },
        error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível entrar. Confira o e-mail e a senha.'))
      });
  }

  fazerLogout(): void {
    this.http.post(`${this.baseUrl}/users/logout`, {}, this.requestOptions)
      .pipe(finalize(() => this.finalizarLogout()))
      .subscribe({ error: () => undefined });
  }

  private finalizarLogout(): void {
    this.isLoggedIn = false;
    this.usuarioLogado = null;
    this.usuarios = [];
    this.materiais = [];
    this.avaliacoes = [];
    this.usuarioEmEdicao = null;
    this.materialEmEdicao = null;
    this.avaliacaoEmEdicao = null;
    this.loginData = { email: '', senha: '' };
    this.limparMensagens();
  }

  alternarCadastro(valor: boolean): void {
    this.mostrarTelaCadastro = valor;
    this.limparMensagens();
  }

  carregarDados(): void {
    this.carregarUsuarios();
    this.carregarMateriais();
    this.carregarAvaliacoes();
  }

  carregarUsuarios(): void {
    this.carregandoUsuarios = true;
    this.http.get<any[]>(`${this.baseUrl}/users`, this.requestOptions)
      .pipe(finalize(() => { this.carregandoUsuarios = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: dados => { this.usuarios = Array.isArray(dados) ? dados : []; this.cdr.detectChanges(); },
        error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível carregar os usuários.'))
      });
  }

  carregarMateriais(): void {
    this.carregandoMateriais = true;
    this.http.get<any[]>(`${this.baseUrl}/materiais`, this.requestOptions)
      .pipe(finalize(() => { this.carregandoMateriais = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: dados => { this.materiais = Array.isArray(dados) ? dados : []; this.cdr.detectChanges(); },
        error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível carregar os materiais.'))
      });
  }

  get materiaisDoAutor(): any[] {
    if (!this.novaAvaliacao.autorId) return [];
    return this.materiais.filter(m => m.usuarioId === this.novaAvaliacao.autorId);
  }

  selecionarAutor(usuarioId: string): void {
    this.novaAvaliacao.autorId = usuarioId;
    this.novaAvaliacao.materialId = '';
  }

  carregarAvaliacoes(): void {
    this.carregandoAvaliacoes = true;
    this.http.get<any[]>(`${this.baseUrl}/avaliacoes`, this.requestOptions)
      .pipe(finalize(() => { this.carregandoAvaliacoes = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: dados => { this.avaliacoes = Array.isArray(dados) ? dados : []; this.cdr.detectChanges(); },
        error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível carregar as avaliações.'))
      });
  }

  salvarNovoUsuario(): void {
    const nome = this.novoUsuario.nome.trim();
    const email = this.novoUsuario.email.trim();
    if (!nome || !email || !this.novoUsuario.senha || !this.novoUsuario.telefone.trim()) {
      this.exibirErro('Preencha nome, e-mail, senha e telefone.');
      return;
    }

    const payload = {
      nome,
      email,
      senha: this.novoUsuario.senha,
      telefone: this.novoUsuario.telefone.trim(),
      ativo: true,
      enderecos: this.novoUsuario.rua.trim() || this.novoUsuario.cidade.trim() || this.novoUsuario.estado.trim()
        ? [{ rua: this.novoUsuario.rua.trim(), cidade: this.novoUsuario.cidade.trim(), estado: this.novoUsuario.estado.trim() }]
        : []
    };

    this.carregandoCadastro = true;
    this.limparMensagens();
    this.http.post(`${this.baseUrl}/users`, payload, { ...this.requestOptions, responseType: 'text' })
      .pipe(finalize(() => { this.carregandoCadastro = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: () => {
          this.novoUsuario = { nome: '', email: '', senha: '', telefone: '', rua: '', cidade: '', estado: '' };
          this.loginData = { email, senha: '' };
          this.mostrarTelaCadastro = false;
          this.exibirSucesso('Cadastro concluído. Entre com o e-mail e a senha cadastrados.');
        },
        error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível cadastrar o usuário.'))
      });
  }

  consultarUsuario(id: string): void {
    this.http.get<any>(`${this.baseUrl}/users/${id}`, this.requestOptions).subscribe({
      next: dado => { this.registroDetalhe = dado; this.tipoDetalhe = 'Usuário'; this.cdr.detectChanges(); },
      error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível consultar o usuário.'))
    });
  }

  abrirEdicaoUsuario(usuario: any): void {
    this.usuarioEmEdicao = {
      ...usuario,
      senha: '',
      telefone: usuario.telefone ?? '',
      enderecos: usuario.enderecos?.length ? usuario.enderecos.map((e: any) => ({ ...e })) : [{ rua: '', cidade: '', estado: '' }]
    };
    this.limparMensagens();
  }

  atualizarUsuario(): void {
    const u = this.usuarioEmEdicao;
    if (!u?.nome?.trim() || !u?.email?.trim() || !u?.senha || !u?.telefone?.trim()) {
      this.exibirErro('Para atualizar o usuário, informe nome, e-mail, telefone e uma nova senha.');
      return;
    }
    const payload = {
      nome: u.nome.trim(),
      email: u.email.trim(),
      senha: u.senha,
      telefone: u.telefone.trim(),
      ativo: u.ativo ?? true,
      enderecos: u.enderecos ?? []
    };
    this.salvando = true;
    this.http.put(`${this.baseUrl}/users/${u.id}`, payload, this.requestOptions)
      .pipe(finalize(() => { this.salvando = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: () => {
          this.usuarioEmEdicao = null;
          this.exibirSucesso('Usuário atualizado com sucesso.');
          this.carregarUsuarios();
        },
        error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível atualizar o usuário.'))
      });
  }

  deletarUsuario(id: string): void {
    if (!confirm('Excluir este usuário? Essa ação não poderá ser desfeita.')) return;
    this.http.delete(`${this.baseUrl}/users/${id}`, this.requestOptions).subscribe({
      next: () => {
        this.exibirSucesso('Usuário excluído.');
        this.carregarUsuarios();
      },
      error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível excluir o usuário.'))
    });
  }

  salvarMaterial(): void {
    const payload = {
      titulo: this.novoMaterial.titulo.trim(),
      descricao: this.novoMaterial.descricao.trim(),
      linkArquivo: this.novoMaterial.linkArquivo.trim()
    };
    if (!payload.titulo || !payload.descricao || !payload.linkArquivo) {
      this.exibirErro('Preencha título, descrição e link do material.');
      return;
    }
    this.salvando = true;
    this.http.post(`${this.baseUrl}/materiais`, payload, this.requestOptions)
      .pipe(finalize(() => { this.salvando = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: () => {
          this.novoMaterial = { titulo: '', descricao: '', linkArquivo: '' };
          this.cdr.detectChanges();
          this.exibirSucesso('Material cadastrado. O usuário responsável foi associado pela sessão.');
          this.carregarMateriais();
              },
        error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível cadastrar o material.'))
      });
  }

  consultarMaterial(id: string): void {
    this.http.get<any>(`${this.baseUrl}/materiais/${id}`, this.requestOptions).subscribe({
      next: dado => { this.registroDetalhe = dado; this.tipoDetalhe = 'Material'; this.cdr.detectChanges(); },
      error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível consultar o material.'))
    });
  }

  consultarAvaliacao(id: string): void {
    this.http.get<any>(`${this.baseUrl}/avaliacoes/${id}`, this.requestOptions).subscribe({
      next: dado => { this.registroDetalhe = dado; this.tipoDetalhe = 'Avaliação'; this.cdr.detectChanges(); },
      error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível consultar a avaliação.'))
    });
  }

  abrirEdicaoMaterial(material: any): void {
    this.materialEmEdicao = { ...material, comentarios: (material.comentarios ?? []).map((c: any) => ({ texto: c.texto ?? '' })) };
    this.limparMensagens();
  }

  atualizarMaterial(): void {
    const m = this.materialEmEdicao;
    if (!m?.titulo?.trim() || !m?.descricao?.trim() || !m?.linkArquivo?.trim()) {
      this.exibirErro('Preencha título, descrição e link do material.');
      return;
    }
    const payload = {
      titulo: m.titulo.trim(),
      descricao: m.descricao.trim(),
      linkArquivo: m.linkArquivo.trim(),
      comentarios: (m.comentarios ?? []).filter((c: any) => c.texto?.trim()).map((c: any) => ({ texto: c.texto.trim() }))
    };
    this.salvando = true;
    this.http.put(`${this.baseUrl}/materiais/${m.id}`, payload, this.requestOptions)
      .pipe(finalize(() => { this.salvando = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: () => {
          this.materialEmEdicao = null;
          this.exibirSucesso('Material atualizado.');
          this.carregarMateriais();
              },
        error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível atualizar o material.'))
      });
  }

  adicionarComentario(material: any): void {
    const texto = (this.novoComentario[material.id] ?? '').trim();
    if (!texto) {
      this.exibirErro('Digite o texto do comentário.');
      return;
    }
    const payload = {
      titulo: material.titulo,
      descricao: material.descricao,
      linkArquivo: material.linkArquivo,
      comentarios: [...(material.comentarios ?? []).map((c: any) => ({ texto: c.texto })), { texto }]
    };
    this.http.put(`${this.baseUrl}/materiais/${material.id}`, payload, this.requestOptions).subscribe({
      next: () => {
        this.novoComentario[material.id] = '';
        this.exibirSucesso('Comentário adicionado.');
        this.carregarMateriais();
          },
      error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível adicionar o comentário.'))
    });
  }

  deletarMaterial(id: string): void {
    if (!confirm('Excluir este material e seus comentários?')) return;
    this.http.delete(`${this.baseUrl}/materiais/${id}`, this.requestOptions).subscribe({
      next: () => {
        this.exibirSucesso('Material excluído.');
        this.carregarMateriais();
          },
      error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível excluir o material.'))
    });
  }

  salvarAvaliacao(): void {
    const payload = this.payloadAvaliacao(this.novaAvaliacao);
    if (!this.novaAvaliacao.autorId || !payload.materialId || !payload.criterios.length || !payload.criterios[0].nome || !Number.isInteger(payload.criterios[0].peso)) {
      this.exibirErro('Selecione o autor, o material e informe um critério com peso inteiro.');
      return;
    }
    if (!Number.isInteger(payload.nota) || payload.nota < 1 || payload.nota > 5) {
      this.exibirErro('A nota deve ser um número inteiro entre 1 e 5.');
      return;
    }
    this.salvando = true;
    this.http.post(`${this.baseUrl}/avaliacoes`, payload, this.requestOptions)
      .pipe(finalize(() => { this.salvando = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: () => {
          this.novaAvaliacao = { nota: 5, comentarioGeral: '', autorId: '', materialId: '', criterioNome: 'Clareza', criterioPeso: 10 };
          this.cdr.detectChanges();
          this.exibirSucesso('Avaliação registrada.');
          this.carregarAvaliacoes();
        },
        error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível registrar a avaliação.'))
      });
  }

  abrirEdicaoAvaliacao(avaliacao: any): void {
    this.avaliacaoEmEdicao = {
      ...avaliacao,
      criterios: (avaliacao.criterios ?? []).map((c: any) => ({ ...c }))
    };
    if (!this.avaliacaoEmEdicao.criterios.length) this.avaliacaoEmEdicao.criterios = [{ nome: '', peso: 1 }];
    this.limparMensagens();
  }

  atualizarAvaliacao(): void {
    const a = this.avaliacaoEmEdicao;
    if (!a || !Number.isInteger(Number(a.nota)) || Number(a.nota) < 1 || Number(a.nota) > 5) {
      this.exibirErro('A nota deve ser um número inteiro entre 1 e 5.');
      return;
    }
    const criterios = (a.criterios ?? []).filter((c: any) => c.nome?.trim()).map((c: any) => ({ nome: c.nome.trim(), peso: Number(c.peso) }));
    if (!criterios.length || criterios.some((c: any) => !Number.isInteger(c.peso))) {
      this.exibirErro('Informe ao menos um critério com peso inteiro.');
      return;
    }
    const payload = { nota: Number(a.nota), comentarioGeral: a.comentarioGeral ?? '', criterios };
    this.salvando = true;
    this.http.put(`${this.baseUrl}/avaliacoes/${a.id}`, payload, this.requestOptions)
      .pipe(finalize(() => { this.salvando = false; this.cdr.detectChanges(); }))
      .subscribe({
        next: () => {
          this.avaliacaoEmEdicao = null;
          this.exibirSucesso('Avaliação atualizada.');
          this.carregarAvaliacoes();
        },
        error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível atualizar a avaliação.'))
      });
  }

  deletarAvaliacao(id: string): void {
    if (!confirm('Excluir esta avaliação?')) return;
    this.http.delete(`${this.baseUrl}/avaliacoes/${id}`, this.requestOptions).subscribe({
      next: () => {
        this.exibirSucesso('Avaliação excluída.');
        this.carregarAvaliacoes();
      },
      error: err => this.exibirErro(this.mensagemDaResposta(err, 'Não foi possível excluir a avaliação.'))
    });
  }

  private payloadAvaliacao(a: any): any {
    return {
      nota: Number(a.nota),
      comentarioGeral: a.comentarioGeral?.trim() ?? '',
      materialId: a.materialId,
      criterios: [{ nome: a.criterioNome?.trim() ?? '', peso: Number(a.criterioPeso) }]
    };
  }

  nomeUsuario(id: string): string {
    return this.usuarios.find(u => u.id === id)?.nome ?? this.resumirId(id);
  }

  tituloMaterial(id: string): string {
    return this.materiais.find(m => m.id === id)?.titulo ?? this.resumirId(id);
  }

  resumirId(id: string): string {
    return id ? `${id.slice(0, 8)}…` : 'Não informado';
  }

  private mensagemDaResposta(err: HttpErrorResponse, padrao: string): string {
    if (err.status === 0) return 'Não foi possível conectar ao servidor em localhost:8080. Verifique se o Spring Boot está em execução e se o CORS permite localhost:4200.';
    if (err.status === 401) return 'Sua sessão não foi reconhecida. Saia e entre novamente.';
    if (typeof err.error === 'string' && err.error.trim()) return err.error;
    return err.error?.message || err.error?.error || `${padrao} Código HTTP: ${err.status}.`;
  }

  private exibirErro(mensagem: string): void {
    this.mensagemErro = mensagem;
    this.mensagemSucesso = '';
    this.cdr.detectChanges();
  }

  private exibirSucesso(mensagem: string): void {
    this.mensagemSucesso = mensagem;
    this.mensagemErro = '';
    this.cdr.detectChanges();
  }

  private limparMensagens(): void {
    this.mensagemErro = '';
    this.mensagemSucesso = '';
    this.cdr.detectChanges();
  }
}
