package br.com.rafaelblomer.businnes;

import br.com.rafaelblomer.businnes.dtos.DadosEmailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailVerificacao(DadosEmailDTO emailDTO) {
        String recipientAddress = emailDTO.emailDestino();
        String subject = "Verificação de Cadastro";
        String confirmationUrl = "http://127.0.0.1:5500/VerificaEmail.html?token=" + emailDTO.token();
        String message = "Olá " + emailDTO.nomeUsuario() + ",\n\n"
                + "Obrigado por se cadastrar no EstoqueALIS. Por favor, clique no link abaixo para ativar sua conta:\n\n"
                + confirmationUrl + "\n\n"
                + "Este link irá expirar em 5 horas.\n\n"
                + "Atenciosamente,\nEquipe EstoqueALIS.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }

    public void enviarEmailTrocaSenha(DadosEmailDTO emailDTO) {
        String recipientAddress = emailDTO.emailDestino();
        String subject = "Alteração de Senha";
        String confirmationUrl = "http://127.0.0.1:5500/alterarsenha.html?token=" + emailDTO.token();
        String message = "Olá " + emailDTO.nomeUsuario() + ",\n\n"
                + "Houve um pedido para alterar sua senha. Se o pedido não foi feito por você, apenas ignore o email.\n\n"
                + "Para alterar sua senha, clique no link abaixo:\n\n"
                + confirmationUrl + "\n\n"
                + "Este link irá expirar em 5 horas.\n\n"
                + "Atenciosamente,\nEquipe EstoqueALIS.";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }
}
