
import pygame
#locals module allows for intuitive key and color names/codes
from pygame.locals import *


def main():

    #initialize screen dimensions along with basic colors
    width = 1020
    height = 1020
    BLACK = (0, 0, 0)
    WHITE = (255, 255, 255)
    RED = (255, 000, 000)

    #initializes pygame library tools
    pygame.init()

    #creates display window
    screen = pygame.display.set_mode((width, height))

    planet_position = [width/2, height/2]
    craft_position = [width/2 + 200, height/2]
    craft_velocity = [0, 0]
    
    #iteration counter to help understand the speed of each iteration
    i = 0

    #event loop
    running = True
    while running:
        i += 1

        #pygame.event list keeps track of all keypress/mouseclicks that occur
        for event in pygame.event.get():
            #closes window when exit button pressed
            if event.type == pygame.QUIT:
                running = False

            #if event log senses a key press, determine which key pressed
            #add to X or Y velocity depending on key press
            speed = 0.1
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_LEFT:
                    craft_velocity[0] += -speed
                    
                if event.key == pygame.K_RIGHT:
                    craft_velocity[0] += speed

                if event.key == pygame.K_UP:
                    craft_velocity[1] += -speed

                if event.key == pygame.K_DOWN:
                    craft_velocity[1] += speed

        craft_position[0] += craft_velocity[0]
        craft_position[1] += craft_velocity[1]
        
        screen.fill(BLACK)
        #draw planet
        pygame.draw.circle(screen, WHITE, planet_position, 50)

        #draw craft
        pygame.draw.circle(screen, RED, craft_position, 5)
        
        pygame.display.set_caption(str(i))
        pygame.display.update()


main()