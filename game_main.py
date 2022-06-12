import math
import time
import pygame
from pygame.locals import *

#defining global variables/tuples here:
G = 6.67408 * (10**-11)

BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
GREY = (200, 200, 200)
RED = (255, 000, 000)
BLUE = (000, 000, 255)
GREEN = (000, 255, 000)
ORANGE = (255, 165, 000)


class Mass:

    def __init__(self, name, mass, radius, stationary, orbital_radius):
        #characteristics about the mass
        self.name = name
        self.mass = mass
        self.radius = radius
        self.stationary = stationary
        self.orbital_radius = orbital_radius

        self.xpos = 0
        self.ypos = 0
        self.xvelo = 0
        self.yvelo = 0

    def __repr__(self):
        return self.name
    
    def gravitational_influence(self, time_interval, relative_center):
        deltax = self.xpos - relative_center.xpos
        deltay = self.ypos - relative_center.ypos
        distance = math.sqrt((deltax**2) + (deltay**2))

        #force of gravity calculated using Newton's Law of Gravition (G defined globally above)
        force_gravity = (G*self.mass*relative_center.mass) / (distance**2)
        #angles drawn from the space craft
        theta = math.atan2(deltay, deltax)

        net_xforce = force_gravity * -math.cos(theta)
        net_yforce = force_gravity * -math.sin(theta)

        net_xaccel = net_xforce / self.mass
        net_yaccel = net_yforce / self.mass

        #calculates the change in velocity over a given time step interval
        self.xvelo += net_xaccel * time_interval
        self.yvelo += net_yaccel * time_interval	

    def deltaPosition(self, time_interval):
        #determines the change in position over the same time step interval
        self.xpos += self.xvelo * time_interval
        self.ypos += self.yvelo * time_interval


class SpaceCraft:

    def __init__(self, name, mass, length, init_xpos=0, init_ypos=0, init_xvelo=0, init_yvelo=0):
        self.name = name
        self.mass = mass
        self.length = length
        self.xpos = init_xpos
        self.ypos = init_ypos
        self.xvelo = init_xvelo
        self.yvelo = init_yvelo

    def __repr__(self):
        return self.name

    def gravitational_influence(self, time_interval):
        #vectors reffered in [magnitude, angle] pairs
        #all angles drawn from the space craft
        gravity_vectors = []

        for body in body_list:
            deltax = self.xpos - body.xpos
            deltay = self.ypos - body.ypos
            distance = math.sqrt((deltax**2) + (deltay**2))

            #force of gravity calculated using Newton's Law of Gravition (G defined globally above)
            force_gravity = (G*self.mass*body.mass) / (distance**2)
            theta = math.atan2(deltay, deltax)

            gravity_vectors.append([force_gravity, theta])

        net_xforce, net_yforce = 0, 0
        for vector in gravity_vectors:
            
            net_xforce += vector[0] * -math.cos(vector[1])
            net_yforce += vector[0] * -math.sin(vector[1])

        net_xaccel = net_xforce / self.mass
        net_yaccel = net_yforce / self.mass

        #calculates the change in velocity over a given time step interval
        self.xvelo += net_xaccel * time_interval
        self.yvelo += net_yaccel * time_interval	


    def deltaPosition(self, time_interval):
        #determines the change in position over the same time step interval
        self.xpos += self.xvelo * time_interval
        self.ypos += self.yvelo * time_interval

    #input display x-y positions along with a scale factor
    def drawCraft(self, screen, xpos, ypos, SCALE_FACTOR):

        tank_length = (1/(SCALE_FACTOR/200)) * self.length * (2/3)
        tank_width = tank_length / 3
        tank_diagonal = math.sqrt(tank_length**2 + tank_width**2)
        tail_diagonal = math.sqrt((tank_length)**2 + (2*tank_width)**2)
        
        #determines the direction to point the craft based on its velocity
        nose_direction = math.atan2(self.yvelo, self.xvelo)
        nose_pos = [xpos + math.cos(nose_direction)*(tank_length*2), ypos + math.sin(nose_direction)*(tank_length*2)]

        #p1-4: four edges of the tank in counterclockwise order starting from top left (when the craft is pointed left)
        p1_angle = nose_direction - math.atan2(tank_width, tank_length)
        p2_angle = nose_direction + math.atan2(tank_width, tank_length)
        p3_angle = p1_angle + math.pi
        p4_angle = p2_angle - math.pi
        
        p1_pos = [xpos + math.cos(p1_angle)*(tank_diagonal), ypos + math.sin(p1_angle)*(tank_diagonal)]
        p2_pos = [xpos + math.cos(p2_angle)*(tank_diagonal), ypos + math.sin(p2_angle)*(tank_diagonal)]
        p3_pos = [xpos + math.cos(p3_angle)*(tank_diagonal), ypos + math.sin(p3_angle)*(tank_diagonal)]
        p4_pos = [xpos + math.cos(p4_angle)*(tank_diagonal), ypos + math.sin(p4_angle)*(tank_diagonal)]

        tank_points = [p1_pos, p2_pos, p3_pos, p4_pos]
        nose_points = [p1_pos, p2_pos, nose_pos]

        tail1_points = [
            [xpos + math.cos(nose_direction - math.pi/2)*(tank_width), ypos + math.sin(nose_direction - math.pi/2)*(tank_width)], 
            [xpos + math.cos(nose_direction - 2.5)*(tail_diagonal), ypos + math.sin(nose_direction - 2.5)*(tail_diagonal)], 
            p4_pos
            ]

        tail2_points = [
            [xpos + math.cos(nose_direction + math.pi/2)*(tank_width), ypos + math.sin(nose_direction + math.pi/2)*(tank_width)], 
            [xpos + math.cos(nose_direction + 2.5)*(tail_diagonal), ypos + math.sin(nose_direction + 2.5)*(tail_diagonal)], 
            p3_pos
            ]

        pygame.draw.polygon(screen, RED, nose_points)
        pygame.draw.polygon(screen, RED, tail1_points)
        pygame.draw.polygon(screen, RED, tail2_points)
        pygame.draw.polygon(screen, GREY, tank_points)
        


def scale_converter(real_distance, SCALE_FACTOR):

    scaled_distance = real_distance / SCALE_FACTOR
    return round(scaled_distance)


#initialize each planetary body
#name, mass, radius, stationary, orbital_radius
earth = Mass("Earth", 5.97*(10**24), 6378, True, 149.6*(10**6))
#sun = Mass("Sun", 1.99*(10**30), 6.96*(10**5), True, 0)

body_list = [earth]#, sun)

#SpaceCraft: name, mass, length, init_xpos=0, init_ypos=0, init_xvelo=0, init_yvelo=0
starship = SpaceCraft("starship", 10, 30, 100000, 0, 0, -19843)



#parameters: display dimensions, simulation width included in the display window, iteration pause interval
def main(screen_width, screen_height, simulation_width, time_step, iteration_pause):

    #display infomation and dimensions
    DP_WIDTH, DP_HEIGHT = screen_width, screen_height    
    REAL_WIDTH = simulation_width #raw width of the display in km
    SCALE_FACTOR = round(REAL_WIDTH / DP_WIDTH) #ratio of km:1 pixel
    TARGET = starship #either a class object or x-y coordinates
    step_options = (0.001, 0.01, 0.1, 0.2) #base, 5 min orbit, 30 second, time-skip

    #starship will always have the 0 index
    target_list = [starship]
    for body in body_list:
        target_list.append(body)
    
    relative_center = None
    for body in body_list:
        if body.stationary == True:
            relative_center = body
            body.xpos, body.ypos = 0, 0
            body.xvelo, body.xvelo = 0, 0
            break
    
    for body in body_list:
        if body != relative_center:
            body.xpos = body.orbital_radius + relative_center.xpos
            body.ypos = relative_center.ypos
            body.xvelo = 0
            body.yvelo = math.sqrt(G*relative_center.mass*(1/body.orbital_radius)) 

    #initializes pygame library tools and display window
    pygame.init()
    screen = pygame.display.set_mode((DP_WIDTH, DP_HEIGHT))
    
    i = 0 #iteration counter to help understand the speed of each iteration
    #event loop
    running = True
    while running:
        i += 1

        #pygame.event list keeps track of all keypress/mouseclicks that occur
        for event in pygame.event.get():
            
            #closes window when exit button pressed
            if event.type == pygame.QUIT:
                running = False

            if event.type == pygame.KEYDOWN:
                
                #zoom in or out using up and down keys, respectively
                if event.key == pygame.K_1:
                    SCALE_FACTOR = round(0.8 * SCALE_FACTOR)
                if event.key == pygame.K_2:
                    SCALE_FACTOR = round(1.2 * SCALE_FACTOR)
                #switch the target object
                if event.key == pygame.K_q:
                    if target_list.index(TARGET)+1 == len(target_list):
                        TARGET = target_list[0]
                    else:
                        TARGET = target_list[target_list.index(TARGET)+1]
                if event.key == pygame.K_e:
                    if target_list.index(TARGET)+1 == len(target_list):
                        TARGET = target_list[0]
                    else:
                        TARGET = target_list[target_list.index(TARGET)-1]   
                #change the simulation speed
                if event.key == pygame.K_z:
                    if step_options.index(time_step)+1 != len(step_options):
                        time_step = step_options[step_options.index(time_step)+1]
                if event.key == pygame.K_x:
                    if step_options.index(time_step) > 0:
                        time_step = step_options[step_options.index(time_step)-1]
                           
        #calculate gravity's influence on each orbiting body
        for body in body_list:
            if body != relative_center:
                body.gravitational_influence(time_step, relative_center)
                body.deltaPosition(time_step)

        starship.gravitational_influence(time_step)
        starship.deltaPosition(time_step)
        
        DP_CENTER_X = DP_WIDTH/2 + (-1 * scale_converter(TARGET.xpos, SCALE_FACTOR))
        DP_CENTER_Y = DP_HEIGHT/2 + (-1 * scale_converter(TARGET.ypos, SCALE_FACTOR))

        screen.fill(BLACK)
        #draw planets
        for body in body_list:
            dp_position = [DP_CENTER_X + scale_converter(body.xpos, SCALE_FACTOR), DP_CENTER_Y + scale_converter(body.ypos, SCALE_FACTOR)]
            #draw.circle(surface, color, center, radius)
            pygame.draw.circle(screen, WHITE, dp_position, scale_converter(body.radius, SCALE_FACTOR))
            pygame.draw.polygon(screen, ORANGE, [dp_position, [dp_position[0]+5, dp_position[1]-10], [dp_position[0]-5, dp_position[1]-10]])
        
        #draw craft
        craft_dp_xpos = DP_CENTER_X + scale_converter(starship.xpos, SCALE_FACTOR)
        craft_dp_ypos = DP_CENTER_Y + scale_converter(starship.ypos, SCALE_FACTOR)
        #pygame.draw.polygon(screen, RED, [craft_dp_position, [craft_dp_position[0]+5, craft_dp_position[1]-10], [craft_dp_position[0]-5, craft_dp_position[1]-10]])
        starship.drawCraft(screen, craft_dp_xpos, craft_dp_ypos, SCALE_FACTOR)

        pygame.display.set_caption(str(i))
        pygame.display.update()
        time.sleep(iteration_pause)

#parameters: real width included in the display window, iteration pause interval
main(1000, 1000, 2000000, 0.01, 0.01)