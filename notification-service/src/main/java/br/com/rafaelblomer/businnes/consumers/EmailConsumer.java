package br.com.rafaelblomer.businnes.consumers;

import br.com.rafaelblomer.businnes.EmailService;
import br.com.rafaelblomer.businnes.dtos.DadosEmailDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = "${broker.queue.email.verificacao}")
    public void emailVerificacaoQueue(@Payload DadosEmailDTO emailDTO) {
        emailService.enviarEmailVerificacao(emailDTO);
    }

    @RabbitListener(queues = "${broker.queue.email.trocasenha}")
    public void trocaSenhaQueue(@Payload DadosEmailDTO emailDTO) {
        emailService.enviarEmailTrocaSenha(emailDTO);
    }

}
