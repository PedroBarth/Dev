from random import randint
from time import sleep
jogador = placarJogador = placarComputador = 0
cores = {'neutra':'\033[m',
         'verde':'\033[32m',
         'vermelho':'\033[31m'}
moeda = ('Cara', 'Coroa')
while jogador != 2:
    print('''---- Cara ou Coroa :) ---- 
    [0] - Cara
    [1] - Coroa
    [2] - Sair do jogo
    ''')
    jogador = int(input('Escolha uma das opções: '))
    if jogador == 0:
        computador = 1
    elif jogador == 1:
        computador = 0
    elif jogador == 2:
        print('GAME OVER!')
    else:
        print('{}Valor Inválido!!'.format(cores['vermelho']))
        print('Por punição, o computador ganha :|{}'.format(cores['neutra']))
        placarComputador += 1
    if jogador == 0 or jogador == 1:
        print('-=-' * 10)
        print('{}O jogador escolheu {}!{}'.format(cores['verde'],moeda[jogador],cores['neutra']))
        print('{}O computador escolheu {}{}!'.format(cores['vermelho'],moeda[computador],cores['neutra']))
        print('-=-' * 10)
        resultado = randint(0,1)
        print('Lançando moeda no ar...')
        sleep(3)
        if resultado == jogador:
            print('{}Caiu {}! Você venceu!{}'.format(cores['verde'],moeda[resultado],cores['neutra']))
            placarJogador += 1
        else:
            print('{}Caiu {}! O computador venceu!{}'.format(cores['vermelho'],moeda[resultado],cores['neutra']))
            placarComputador += 1
print('Placar do Jogador: {}{}{}'.format(cores['verde'],placarJogador,cores['neutra']))
print('Placar do Computador: {}{}{}'.format(cores['vermelho'],placarComputador,cores['neutra']))
print('Carregando resultado...')
sleep(2)
if placarJogador > placarComputador:
    print('{}JOGADOR VENCEU!{}'.format(cores['verde'],cores['neutra']))
elif placarJogador < placarComputador:
    print('{}COMPUTADOR VENCEU!{}'.format(cores['vermelho'],cores['neutra']))
else:
    print('EMPATE!!')
print('FIM!')




